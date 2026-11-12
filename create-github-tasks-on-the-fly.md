# Milestones (drops)
gh api repos/:owner/:repo/milestones -f title="D1 Walking skeleton" -f description="Spec in repo, MCP server serving it, one change made via agent"
gh api repos/:owner/:repo/milestones -f title="D2 Repeatable workflow" -f description="One command takes a spec id to an open PR"

# Labels (epics)
gh label create E2 --description "Spec store and format" --color 1d76db
gh label create E3 --description "MCP server - read-only tools" --color 5319e7
gh label create E4 --description "Spec-driven workflow" --color 0e8a16
gh label create E7 --description "Token economics and observability" --color fbca04
gh label create sprint-1 --color ededed
gh label create sprint-2 --color ededed

D1="D1 Walking skeleton"
mk() { gh issue create --title "$1" --label "$2" --label "$3" --milestone "$D1" --body "$4"; }

mk "T-01 Define spec front-matter schema + TEMPLATE.md" E2 sprint-1 "Estimate: 1h"
mk "T-02 Write three real specs for my-site" E2 sprint-1 "Estimate: 2h. Small, independent; acceptance criteria as checkable statements."
mk "T-03 Write context/my-site.md" E2 sprint-1 "Estimate: 1h. Module map, entry points, build and test commands."
mk "T-04 Scaffold Spring Boot project with spring-ai-starter-mcp-server" E3 sprint-1 "Estimate: 1h"
mk "T-05 Front-matter parser and spec repository" E3 sprint-1 "Estimate: 2h. Specs folder configurable via env var."
mk "T-06 Implement listSpecs and getSpec" E3 sprint-1 "Estimate: 2h. Write tool descriptions deliberately."
mk "T-07 Configure stdio (banner off, no web, log to file)" E3 sprint-1 "Estimate: 0.5h"
mk "T-08 Register with claude mcp add; confirm connected" E3 sprint-1 "Estimate: 0.5h"
mk "T-09 Discovery test: do the tools get called unprompted?" E3 sprint-1 "Estimate: 1h. The real acceptance test of sprint 1."

mk "T-10 Implement getServiceContext" E3 sprint-2 "Estimate: 1.5h"
mk "T-11 Unit tests for parser and tools" E3 sprint-2 "Estimate: 2h"
mk "T-12 Measure baseline token cost" E7 sprint-2 "Estimate: 1h. Tool definitions per turn; full spec run."
mk "T-13 Revise tool descriptions; re-run T-09" E3 sprint-2 "Estimate: 2h"
mk "T-14 End-to-end manual run: spec id to open PR" E4 sprint-2 "Estimate: 3h"
mk "T-15 Drop 1 retro; update SDD decision log" E7 sprint-2 "Estimate: 1h"