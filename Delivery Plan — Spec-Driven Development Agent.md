# Delivery Plan — Spec-Driven Development Agent

Nov 10, 2026 · Owner @Amr · Companion to the SDD

## 1. Approach

**Cadence:** two-week sprints. **Capacity:** roughly 10 hours per sprint — evenings and weekends alongside full-time work. Estimates below are sized to that, deliberately conservatively; a plan that assumes 25 hours a sprint will be abandoned by sprint three.

**Drops** are the releases that matter. A drop is done when it works end to end, not when its tasks are closed. Sprints inside a drop can slip; the drop boundary is where progress is judged.

**Planning horizon:** sprints 1 and 2 are planned to task level. Everything beyond that is held at epic level on purpose — what is learned in Drop 1 will change the shape of Drop 2, and planning it now would mean planning it twice.

## 2. Drops

| Drop | Name | Outcome | Sprints | Status |
| --- | --- | --- | --- | --- |
| D0 | Foundation | Local environment, repo, CI, site deploying | — | Done |
| D1 | Walking skeleton | A spec in the repo, an MCP server serving it, a change made by hand-driven agent | 1–2 | In progress |
| D2 | Repeatable workflow | One command takes a spec id to an open pull request, with a review subagent | 3–4 | Planned |
| D3 | Unattended | A merged spec triggers a run with no terminal attached | 5–6 | Planned |
| D4 | Real codebase | Context tools over an existing system of meaningful size | 7+ | Planned |

## 3. Epics

| Epic | Title | Drop | Why it exists |
| --- | --- | --- | --- |
| E1 | Environment and baseline | D0 | A target repository the agent can safely change |
| E2 | Spec Kit setup and workflow | D1 | A machine-readable unit of work |
| E3 | MCP server — read-only tools | D1 | The protocol learning goal; context delivery |
| E4 | Spec-driven workflow | D2 | Turn a manual sequence into one repeatable command |
| E5 | Unattended execution | D3 | Remove the human from the trigger, not from review |
| E6 | Existing-system context | D4 | The actual hard problem: context at scale |
| E7 | Token economics and observability | All | Measure cost per spec; without this, D3 is unaffordable |
| E8 | Documentation and reference | All | The reference worked from during the build; updated at every drop so it never trails the code |

## 4. Sprint 1

**Goal:** Spec Kit installed and initialised, a context file served by the MCP server’s `getServiceContext` tool, and Claude Code calls it without being told to.

**Sprint 1 progress (2026-11-11):**

| Task | Status | Notes |
| --- | --- | --- |
| T-01 Define spec schema | ❌ Dropped (Spec Kit) |  |
| T-02 Write three specs | ❌ Dropped (Spec Kit) |  |
| T-03 Write context/my-site.md | ⬜ Not started |  |
| T-04 Scaffold Spring Boot project | ✅ Done | pom.xml, main class, SpecTools stub |
| T-05 Front-matter parser | ❌ Dropped (Spec Kit) | Replaced by Spec Kit's own format |
| T-06 Implement listSpecs and getSpec | ❌ Dropped (Spec Kit) | listSpecs/getSpec replaced by Spec Kit skills; server keeps getServiceContext only |
| T-07 Stdio configuration | ✅ Done | Banner off, logback file-only, clean stdout |
| T-08 Register with Claude Code | ✅ Done | sdd-specs ✔ Connected |
| T-09 Discovery test | ⏳ Next | Scope narrowed to getServiceContext only (Spec Kit handles spec discovery) |
| T-16 Write README | ⬜ Not started |  |
| T-SK1 Install Spec Kit | ⬜ Not started | uv tool install + specify init |
| T-SK2 Run constitution | ⬜ Not started | Project-specific rules for my-site |
| T-SK3 Create first spec | ⬜ Not started | Validates the full Spec Kit workflow end to end |

| Id | Epic | Task | Est |
| --- | --- | --- | --- |
| T-01 | E2 | `❌ Dropped — Spec Kit owns the spec schema` | 1h |
| T-02 | E2 | `❌ Dropped — First specs created through Spec Kit’s /speckit.specify workflow` | 2h |
| T-03 | E2 | Write `context/my-site.md`: module map, entry points, build and test commands | 1h |
| T-04 | E3 | Scaffold the Spring Boot project with `spring-ai-starter-mcp-server` | 1h |
| T-05 | E3 | ❌ Dropped — Spec Kit handles parsing and format enforcement | 2h |
| T-06 | E3 | `❌ Dropped — listSpecs/getSpec replaced by Spec Kit skills; MCP server keeps getServiceContext on`ly | 2h |
| T-07 | E3 | Configure stdio: banner off, web application type none, logging to file | 0.5h |
| T-08 | E3 | Register with `claude mcp add --scope user`; confirm `claude mcp list` reports connected | 0.5h |
| T-09 | E3 | Discovery test: ask a question that needs the tools without naming them; record whether they were called | 1h |
| T-16 | E8 | Write the README for the MCP server: purpose, build, run, register, troubleshooting | 0.5h |
| T-SK1 | E2 | Install Spec Kit (uv tool install) and initialise for Claude Code (specify init my-site --integration claude) | 0.5h |
| T-SK2 | E2 | Run /speckit.constitution to set project-specific rules for my-site | 0.5h |
| T-SK3 | E2 | Create first spec using /speckit.specify; validate the full Spec Kit workflow end to end | 1h |

**Sprint 1 total: 7h** (revised down from 11.5h after dropping T-01, T-02, T-05, T-06 and adding T-SK1–SK3). Well within 10h capacity.

T-09 is the real acceptance test of the sprint, not T-08. A server that connects but is never called has failed at the thing it exists to do.

## 5. Sprint 2

**Goal:** one spec goes from file to open pull request, driven by hand, with the cost of doing so measured.

| Id | Epic | Task | Est |
| --- | --- | --- | --- |
| T-10 | E3 | Implement `getServiceContext` (the server's only tool) reading `context/<name>.md` | 1.5h |
| T-11 | E3 | Unit tests for getServiceContext (the only custom tool) | 2h |
| T-12 | E7 | Measure baseline: tokens consumed by tool definitions per turn, and per full spec run | 1h |
| T-13 | E3 | Revise tool descriptions against the discovery results; re-run T-09 | 2h |
| T-14 | E4 | End-to-end manual run: spec id in, branch pushed and pull request opened on `my-site` | 3h |
| T-15 | E8 | Drop 1 documentation checkpoint and retro — run the checklist in section 10, write the drop log entry, record what changed in the SDD decision log and open questions | 1.5h |

**Sprint 2 total: 11h.**

## 6. Beyond Drop 1

Held at epic level until Drop 1 is finished.

| Epic | Likely first tasks |
| --- | --- |
| E4 | `Spec Kit slash commands (/speckit.implement, /speckit.converge)`; review subagent that sees the diff but not the implementation reasoning; test-runner subagent |
| E5 | Choose the runner (GitHub Actions vs local daemon); resolve the transport decision D6; secrets handling; trigger on spec merge or label |
| E6 | Decide generated vs hand-written service context; module map extraction; granularity experiments |
| E7 | Cost per spec tracked across drops; tool-call log attached to every pull request |
| E8 | A documentation checkpoint closes every drop (D2, D3, D4 each get one task, same checklist as T-15); the README and setup reference gain a section per new capability |

## 7. Definition of done

**Task:** code merged to the default branch, tests pass, and anything learned that contradicts the SDD is written into its decision log or open questions.

**Drop:** the outcome in the drops table is demonstrable from a clean checkout, by following written steps, without recalling anything that lives only in the author's head.

**Documentation:** a drop is not closed until its documentation checkpoint (section 10) is complete and its drop log entry (section 11) is written.

## 8. Tooling decision

| Concern | Tool | Rationale |
| --- | --- | --- |
| Specs | GitHub Spec Kit + markdown in the target repository | Spec Kit provides a battle-tested workflow (constitution → specify → implement); specs still version with the code as markdown. Eliminates custom schema, parser and spec-reading tools. |
| Tasks and epics | GitHub Issues with a Project board | Already authenticated via `gh`; an agent can read and write issues through the same CLI it uses for pull requests. No extra integration to build in D3. |
| Design documents | This SDD and plan, exported where needed | Living documents; export to PDF or Word when something needs to leave. |
| Chat | Not a tracker | Useful for notifications; it holds no state worth querying. |

The decision that matters is **keeping specs in the repository rather than in a tracker.** It is what makes the MCP server a file reader instead of an API client, and what makes a CI trigger on spec merge possible without a webhook. Should the spec store later need to live in Jira, the tool contracts in the SDD do not change — only their implementation does, which is the point of defining them as contracts.

## 9. Mapping to a tracker

| This document | GitHub Issues |
| --- | --- |
| Drop | Milestone |
| Epic | Label `epic:E3`, or a tracking issue with a task list |
| Task | Issue, titled as written here, estimate in the body |
| Sprint | Project board iteration, or a `sprint-1` label |

Worth doing once Drop 1 is underway, not before — a board with nothing moving on it is overhead.

## 10. Documentation protocol

Documentation is a deliverable with its own epic (E8), not something done if time allows. Its purpose is practical: these documents are what you work from while building the real project, so a stale one is worse than none.

### Documents and when each is updated

| Document | Lives in | Update when |
| --- | --- | --- |
| Setup reference (Claude Code local setup, deployment, troubleshooting log) | Claude Docs | Any new install step or command; every error hit, written down when it happens |
| SDD | Claude Docs | A decision is made or reversed, an open question is answered, an interface changes |
| Delivery plan (this document) | Claude Docs | End of each drop: statuses, actual hours, and the next drop's epics broken into tasks |
| README of the MCP server | Repository | With the code that changes it; verified at each checkpoint |
| Specs and context files | Repository | With the code. These are part of the system, not documentation about it |

### Documentation checkpoint

Run at the end of every drop. About 1–1.5 hours; sized into the sprint, not squeezed in after it.

1. **Drops table:** set the status and note actual against estimated hours.
2. **SDD:** add or update decision log entries, answer or add open questions, update tool contracts and the spec schema if they changed.
3. **Setup reference:** add new steps and changed commands; add a troubleshooting row for every issue hit (symptom, cause, fix).
4. **README:** run its build, run and register commands literally from a clean checkout. If they fail, fix the README before closing the drop.
5. **Measurements:** record token cost figures in the drop log.
6. **Re-plan:** turn the next drop's epics into tasks, using what was learned.
7. **Commit:** documentation changes are committed, and the checkpoint issue is closed.

One rule makes the checkpoint cheap: **write the troubleshooting row at the moment of the error**, not at the end. At the checkpoint you then only review, not reconstruct from memory.

## 11. Drop log

One row per drop, written at its checkpoint. The cost column is what makes the later drops plannable.

| Drop | Closed | Planned / actual hours | What changed from the plan | What was learned | Token cost per spec |
| --- | --- | --- | --- | --- | --- |
| D0 | 2026-11-10 | — | Environment issues hit and fixed; site deploys to GitHub Pages. See the setup reference troubleshooting log | Native install needs `~/.local/bin` on PATH; `gh repo create --push` needs a first commit; GitHub Pages needs a public repository on the free plan | — |
| D1 | — | — | — | — | — |
| D2 | — | — | — | — | — |
| D3 | — | — | — | — | — |
| D4 | — | — | — | — | — |
