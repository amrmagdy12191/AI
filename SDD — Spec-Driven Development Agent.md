# SDD — Spec-Driven Development Agent

Nov 10, 2026 · Owner @Amr · Status: Draft v0.1

## 1. Purpose and context

Feature work on an existing system follows a repeatable shape: read the spec, locate the affected code, make the change, test it, open a PR for review. The mechanical parts of that loop consume senior engineering time without using senior engineering judgement.

This project builds a system where a written specification is the unit of work an agent can act on: given a spec identifier, the agent gathers the context it needs about the existing codebase through defined tool interfaces, implements the change, and opens a pull request for human review.

It is also a deliberate learning vehicle for the Model Context Protocol (MCP), agent orchestration, and context economics — so phases are ordered by what they teach, not only by what they deliver.

## 2. Goals and non-goals

**Goals**

- A spec, written once in a defined format, is sufficient input for an agent to produce a reviewable pull request
- Context about the existing system reaches the agent through narrow, purpose-built tools rather than letting it read the whole repository
- The loop runs unattended (triggered by a merged or labelled spec) by the final phase
- Token cost per spec is measured, not guessed

**Non-goals**

- No autonomous merging. A human reviews and merges every pull request, in every phase.
- Not a replacement for design work. The agent implements specs; it does not decide what should be built.
- No multi-tenant or team-wide deployment in scope. Single developer, single machine, then single CI pipeline.
- No fine-tuning or custom models.

## 3. Stakeholders

| Role | Who | Interest |
| --- | --- | --- |
| Owner / developer | Amr | Builds and operates the system; sole reviewer of generated PRs |
| Agent runtime | Claude Code, later the Claude Agent SDK | Consumes specs and context tools, produces code |
| Target system | `my-site`, later a real service | Receives the changes |

## 4. Architecture overview

&#91;embedded content: architecture overview · 6 components, spec to merge\]

Specs enter through **GitHub Spec Kit**, which owns the spec format, authoring workflow, planning and task breakdown — all driven by skills installed into `.claude/skills/`. Curated context about the target system enters through the MCP server's single read-only tool (`getServiceContext`). The agent plans and edits in a working copy, runs the tests, pushes a branch and opens a pull request. Nothing it produces reaches the default branch without a human merging it, and a merged change closes the loop by moving the spec's status to done.

The only components built from scratch are the MCP server (context delivery) and the Spec Kit constitution (project-specific rules). Everything else is an existing tool used for what it is already good at — Spec Kit for the spec workflow, Git for history, the GitHub CLI for pull requests, CI for tests, the agent runtime for code. That boundary is deliberate: it keeps the learning surface on the protocol and on context design rather than on integration work.

## 5. Component design

### 5.1 Spec store

Spec management — format, authoring, planning and task breakdown — is handled by **GitHub Spec Kit**, an open-source SDD toolkit. Spec Kit installs skills into `.claude/skills/` and provides a structured workflow: `constitution` → `specify` → `clarify` → `plan` → `checklist` → `tasks` → `implement` → `converge`. Specs still live as markdown in the target repository (preserving the versioning and diffability benefits of D1), but their schema and lifecycle are owned by Spec Kit rather than a custom template.

The MCP server no longer reads or parses specs. That responsibility belongs entirely to Spec Kit's own skills. The server's sole role is context delivery (see 5.2).

### 5.2 MCP server

A Spring Boot application exposing a single read-only tool (`getServiceContext`) over the stdio transport. Claude Code spawns it as a subprocess and speaks JSON-RPC over stdin/stdout; there is no port, no authentication and nothing to deploy.

Responsibilities:

- Serve curated context about the target system (module map, public API surface, key dependencies, build and test commands) — the information Spec Kit does not provide and that the agent's own file tools handle less efficiently than a purpose-built summary
- Nothing else. The server does not write code, call the model, or mutate state.

Stdio imposes one hard constraint: **stdout is the protocol channel.** The application banner must be off and all logging directed to a file. A single stray write to stdout corrupts the JSON-RPC frame and the server appears to fail.

### 5.3 Agent runtime

Phase 1–2 the runtime is Claude Code, driven interactively and then by a custom slash command that sequences the work. Phase 3 moves to the Claude Agent SDK or Claude Code invoked from GitHub Actions, so a merged spec can trigger a run with no terminal attached.

Subagents separate concerns that otherwise contaminate each other: one implements, a second reviews the diff without having seen the implementation reasoning, a third runs and interprets tests.

### 5.4 Target repository

Standard Git repository with CI. The agent interacts with it through `git` and the GitHub CLI (`gh`) rather than a GitHub MCP server — the CLI covers branch, commit, push and PR creation at near-zero context cost, where a connector's tool definitions are paid for on every turn.

## 6. Interfaces

### 6.1 MCP tool contracts

| Tool | Input | Returns | Called when |
| --- | --- | --- | --- |
| `getServiceContext` | `name` | Module map, public API, key dependencies | Before touching code in a service |

Tool descriptions are part of the interface, not documentation. They are the only signal the model has when deciding whether to call a tool, and they occupy context on every turn — so they are specified, reviewed and revised like any other contract.

### 6.2 Spec format (managed by Spec Kit)

The spec format — front matter fields, body sections, status lifecycle — is defined and enforced by Spec Kit. The project's `speckit.constitution.md` can constrain these (e.g. requiring an `acceptance_criteria` section or a `service` field that maps to `getServiceContext`), but the base schema is Spec Kit's, not ours. This eliminates the custom schema table and parser that were previously planned (T-05).

## 7. Deployment view

| Phase | Where the agent runs | Where the MCP server runs | Trigger |
| --- | --- | --- | --- |
| 1–2 | Local terminal | Local subprocess (stdio) | Human types a command |
| 3 | GitHub Actions runner or local daemon | Same runner, or HTTP transport | Spec merged or labelled |
| 4 | As phase 3 | Likely HTTP, shared | As phase 3 |

The transport change in phase 3 is the first real architectural decision point: stdio cannot serve a remote runner, so the server either ships alongside the job or moves to Streamable HTTP with authentication.

## 8. Non-functional requirements

| Requirement | Target | How it is verified |
| --- | --- | --- |
| Token cost per spec | Measured and trending down across phases | Session cost reported per run |
| Context overhead | Tool definitions under \~1k tokens total | Measured in-session |
| Determinism | Same spec twice produces equivalent, not identical, output; tests pass both times | Repeat runs on a fixed spec |
| Latency | Under 10 minutes per small spec, unattended | CI job duration |
| Observability | Every run leaves a log of tool calls made and files touched | Server log plus PR description |
| Safety | No path to merge without human approval | Branch protection |

## 9. Security and guardrails

- The agent operates on a feature branch only; the default branch is protected and requires review
- Credentials reach the runtime as environment variables or CI secrets, never in specs or committed configuration
- The MCP server is read-only by design — no tool mutates the repository, so a prompt-injected instruction in a spec cannot reach a write path through it
- Specs are treated as trusted input in phase 1–2 (the owner writes them). Before any phase where others can submit specs, spec content must be treated as untrusted and the implementing agent's permissions scoped accordingly
- Repository scope is limited to the target repository; the agent's GitHub token carries no organisation-wide rights

## 10. Risks

| Risk | Impact | Mitigation |
| --- | --- | --- |
| Tool descriptions too vague — agent never calls the tool | System appears broken; wrong diagnosis leads to rewriting working code | Test discovery with indirect prompts that name no tool |
| Context cost grows faster than value | Unattended runs become expensive | Measure per phase; prefer narrow structured returns over whole files |
| Agent produces plausible but wrong changes | Review burden exceeds time saved | Acceptance criteria as checkable statements; separate review subagent; tests required before PR |
| Over-engineering the MCP server early | Learning goal lost to integration work | Keep phase 1 tools read-only and file-based; no tracker integration until it is the constraint |
| Spec format churn | Rework across specs and server | Front matter kept minimal; body sections free-form |

## 11. Decision log

| # | Decision | Rationale | Status |
| --- | --- | --- | --- |
| D1 | Specs as markdown in the target repository | Versions with the code; readable by a file tool; diffable in review | Accepted |
| D2 | Java / Spring Boot for the MCP server | Owner's primary stack; keeps attention on the protocol, not the language | Accepted |
| D3 | stdio transport for phases 1–2 | No deployment, no auth; lowest-friction path to a working server | Accepted |
| D4 | `git` and `gh` CLI instead of a GitHub MCP server | Same capability at near-zero context cost | Accepted |
| D5 | Human review mandatory in all phases | Generated changes are proposals, not commits | Accepted |
| D6 | Transport for phase 3 (stdio-in-job vs HTTP) | Deferred until the runner is chosen | Open |
| D7 | Java 26 (the local default), Spring Boot 4.1.x, Spring AI 2.0.x | Already installed; inside Boot 4.1's supported range (Java 17 to 26); Spring AI 2.0 targets Boot 4.0 and 4.1. Java 26 is not an LTS release, so revisit after Drop 1 and move to 25 LTS if tooling or support becomes a problem | Accepted |
| D8 | Adopt GitHub Spec Kit for spec workflow | Battle-tested workflow out of the box; constitution → specify → implement cycle handles spec format, writing and planning; MCP server narrows to context delivery only; less custom code to maintain | Accepted |

## 12. Open questions

- Does `getServiceContext` return hand-written context, or context generated from the codebase? Hand-written is faster to start and goes stale; generated is the real answer at phase 4 scale.
- What is the right granularity of returned context — whole documents, or structured fields the agent composes? This determines token cost more than any other choice.
- Where do test results enter the loop: does the agent run tests itself, or read CI results back after pushing?
- How much of Spec Kit's default constitution needs project-specific overrides, and does the overhead of maintaining a detailed constitution pay for itself on a single-developer project?
