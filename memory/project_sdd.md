---
name: project-sdd
description: SDD agent project current status, architecture decisions, and next steps
metadata:
  type: project
---

**Project:** Spec-Driven Development Agent — a system where a written spec is sufficient input
for an agent to produce a reviewable pull request. Learning vehicle for MCP + agent orchestration.

**Target system:** `my-site` — a Vite + plain HTML/CSS personal site deploying to GitHub Pages.

**Key paths:**
- MCP server: `/Users/amrmagdy/mine/Projects/AI/sdd-mcp-server/`
- Target site: `/Users/amrmagdy/mine/Projects/AI/my-site/`
- Specs: `my-site/specs/`
- Service context files: `my-site/context/`

**Architecture decisions (from SDD doc):**
- D2: Java/Spring Boot for MCP server
- D3: stdio transport for phases 1–2
- D4: `git` + `gh` CLI instead of GitHub MCP
- D7: Java 26, Spring Boot 4.1.x, Spring AI 2.0.x
- D8: GitHub Spec Kit for spec workflow

**Sprint 1 status (as of 2026-11-12):**

| Task | Status |
| --- | --- |
| T-04 Scaffold Spring Boot project | ✅ Done |
| T-07 Stdio configuration | ✅ Done |
| T-08 Register with Claude Code | ✅ Done |
| T-SK1 Install Spec Kit | ✅ Done (skills in my-site/.claude/skills/) |
| T-SK3 Create first spec | ✅ Done (SPEC-001-dark-mode.md exists) |
| T-03 Write context/my-site.md | ✅ Done (2026-11-12) |
| T-SK2 Run speckit.constitution | ✅ Done (1.0.0, 2026-11-12) |
| T-10 Implement getServiceContext | ✅ Done (2026-11-12) |
| T-11 Unit tests for getServiceContext | ✅ Done — 3 tests pass |
| T-16 Write MCP server README | ✅ Done (2026-11-12) |
| T-09 Discovery test | ⚠️ Blocked — needs fresh Claude Code session to reload jar |

**Sprint 2 remaining:**
- T-12: Measure baseline token cost per turn / per spec run
- T-13: Revise tool descriptions based on T-09 results; re-run discovery test
- T-14: End-to-end manual run — spec id in, PR opened on my-site
- T-15: Drop 1 doc checkpoint + retro

**Known issue:** `claude mcp restart` does not exist. After rebuilding the MCP server jar,
start a fresh Claude Code session so the server restarts and advertises the new `getServiceContext`
tool (old `listSpecs` tool is in the old jar — dropped in favour of Spec Kit).

**Why:** The SDD plan orders tasks by what they teach. T-09 (discovery test) is the real
acceptance test of Sprint 1 — a server that connects but is never called has failed at its
purpose. Run it in a fresh session once the jar is loaded.
