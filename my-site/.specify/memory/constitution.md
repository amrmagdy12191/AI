<!--
SYNC IMPACT REPORT (remove before committing)
=============================================
Version change:  0.0.0 (unfilled template) → 1.0.0
Added sections:  Core Principles (all 5), Spec Format, Review & Merge Workflow, Governance
Removed:         All placeholder comments and bracket tokens
Follow-up TODOs: None
-->

# my-site Constitution

## Core Principles

### I. Spec-Driven Changes

Every feature or change to this site MUST begin as a spec file in `specs/`.
No code changes are made without a corresponding spec that includes `id`, `title`, `status`,
`service`, and `acceptance_criteria`. Ad-hoc edits outside a spec workflow are not permitted.

Rationale: the spec is the unit of work the agent acts on. Without a spec there is no
acceptance criteria and no way to verify the change is done.

### II. Human Review on Every Merge

The agent MUST NOT merge its own pull requests. Every PR created by the agent waits for
human review and manual merge. Branch protection on the default branch enforces this.

Rationale: generated changes are proposals. The human reviewer is the last quality gate.
This cannot be relaxed in any phase.

### III. Build Must Pass

`npm run build` MUST succeed before a PR is opened. This is the sole automated correctness
gate — the site has no test suite. A PR that breaks the build MUST NOT be opened.

Rationale: `dist/` is what deploys. A broken build means a broken site.

### IV. Acceptance Criteria Are the Contract

The `acceptance_criteria` section of the spec is the definition of done. Each criterion
MUST be checkable from the outside (observable in the browser or in the built output).
Vague criteria ("it looks good") are not valid — they MUST be rewritten to be specific and
verifiable before implementation begins.

Rationale: the agent cannot know the feature is done without a checkable contract.
Ambiguous criteria shift the burden to the reviewer.

### V. Context Before Code

Before touching any file in a service, the agent MUST call `getServiceContext` with the
service name from the spec's `service` field. This delivers the module map, design
decisions, and build commands so the agent does not need to explore the repository by hand.

Rationale: narrow, purpose-built context is cheaper and more reliable than open-ended
file exploration. Skipping this step leads to changes that contradict existing design decisions.

## Spec Format

Required front-matter fields for every spec:

| Field | Type | Notes |
| --- | --- | --- |
| `id` | string | Unique, e.g. `SPEC-001`. Never reused, even after a spec is closed. |
| `title` | string | Imperative phrase, e.g. "Add dark mode toggle to header" |
| `status` | enum | `open` → `in-progress` → `done` |
| `service` | string | Maps to a `context/<service>.md` file and to `getServiceContext` |
| `acceptance_criteria` | section | Bullet list; each item is independently verifiable |

Optional fields (`depends_on`, `out_of_scope`) are allowed but not required.

The spec body is otherwise free-form. Keep it minimal — the acceptance criteria carry the
weight; prose sections exist only when they prevent misunderstanding.

## Review and Merge Workflow

1. Agent reads the spec, calls `getServiceContext`, implements changes on a feature branch.
2. Agent runs `npm run build`; if it fails the agent fixes the issue before continuing.
3. Agent opens a PR. The PR description lists: spec id, acceptance criteria, and files changed.
4. Human reviews the diff against the acceptance criteria.
5. Human merges (or requests changes). No autonomous merge in any phase.
6. After merge, the spec's `status` is updated to `done`.

## Governance

This constitution supersedes all other written or verbal guidance about how changes are
made to my-site. When a decision contradicts this document, this document wins unless it is
amended.

**Amendment procedure**: edit this file, increment the version, and commit the change with
a message of the form `docs: amend constitution to vX.Y.Z (<reason>)`. No approval process
is required for a single-developer project, but the reason MUST be recorded in the commit.

**Versioning policy**:
- MAJOR: a principle is removed, reversed, or redefined in a backward-incompatible way.
- MINOR: a new principle or section is added.
- PATCH: clarification, wording, or non-semantic refinement.

**Compliance review**: at every drop checkpoint (see the Delivery Plan), verify that the
constitution still matches how the system actually operates. Update it if it has drifted.

**Version**: 1.0.0 | **Ratified**: 2026-11-12 | **Last Amended**: 2026-11-12
