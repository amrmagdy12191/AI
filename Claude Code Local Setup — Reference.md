# Claude Code Local Setup — Reference

Nov 8, 2026

## Goal

Set up a local environment to work with Claude Code, connect it to GitHub, and build toward a system that takes a Software Design Document (SDD) and develops against an existing codebase via an MCP server.

**Recommended approach:** run Claude Code locally on a Pro/Max subscription (flat monthly cost, no per-token metering) using plain `git` and GitHub's `gh` CLI for GitHub — no MCP server yet. Add an MCP server later, once building one is itself the thing being learned.

## What's done so far

| Step | What it gives you | Status |
| --- | --- | --- |
| 1. Install Claude Code | AI coding assistant in your terminal — edits files, runs commands, commits | ✅ Done |
| 2. Git + GitHub CLI | Version control and GitHub access without leaving the terminal | ✅ Done |
| 3. Create repo and push | Code on GitHub, accessible anywhere, ready for CI/CD | ✅ Done |
| 4. Start Claude Code | Claude sees your project, generates code with full context | ✅ Done |
| 5. Deploy to GitHub Pages | Live site that auto-deploys on every push — a visible result and a target repo | ✅ Done |
| Stage 2 — Build MCP server | Claude Code calls your Java tools for specs and context — the core learning goal | ✅ Builds and connects |
| Stage 2 — Discovery test | Proves Claude calls getServiceContext unprompted — the real acceptance test | ⏳ Next |

**Next action:** install Spec Kit and run the constitution. Then run the T-09 discovery test — open Claude Code in `my-site`, ask *"What do you know about this project's architecture?"* and see whether Claude calls `getServiceContext` on its own.

## Step 1 — Install Claude Code

**macOS / Linux / WSL:**

```bash
curl -fsSL https://claude.ai/install.sh | bash
```

**Windows PowerShell:**

```powershell
irm https://claude.ai/install.ps1 | iex
```

The installer places the binary at `~/.local/bin/claude`. If `claude --version` returns "command not found" afterward, that folder isn't on PATH yet. Fix (zsh):

```bash
echo 'export PATH="$HOME/.local/bin:$PATH"' >> ~/.zshrc && source ~/.zshrc
claude --version
```

If it still fails:

- Confirm the binary exists: `ls -l ~/.local/bin/claude`
- Confirm the PATH line saved: `grep local/bin ~/.zshrc`
- Some shell frameworks (Oh My Zsh, Powerlevel10k) reset PATH later in `.zshrc` — move the export line to the bottom of the file and open a new terminal tab
- Test directly without PATH: `~/.local/bin/claude --version`

## Step 2 — Install and sign in to GitHub's tools

**macOS (Homebrew):**

```bash
brew install git gh
git --version
gh --version
```

If Homebrew itself is missing:

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

then run the `eval ...` lines it prints under "Next steps".

**Linux (Ubuntu/WSL):**

```bash
sudo apt install git gh
```

If apt can't find `gh`, follow GitHub's apt repository setup at cli.github.com.

**Configure git identity:**

```bash
git config --global user.name "Amr Magdy"
git config --global user.email "your-github-email@example.com"
git config --global init.defaultBranch main
```

Use the email tied to your GitHub account (or its `@users.noreply.github.com` address if "keep my email private" is enabled).

**Sign in:**

```bash
gh auth login
```

Prompts to choose:

| Prompt | Choice |
| --- | --- |
| Where do you use GitHub? | GitHub.com |
| Preferred protocol | HTTPS |
| Authenticate Git with GitHub credentials? | Yes |
| How to authenticate | Login with a web browser |

This also sets `gh` as git's credential helper, so `git push` works without extra token setup.

**Verify:**

```bash
gh auth status
```

## Step 3 — Create the project and push to GitHub

`gh repo create --push` needs at least one commit to push, so commit before creating the repo:

```bash
mkdir my-site && cd my-site
git init
echo "# my-site" > README.md
git add README.md
git commit -m "Initial commit"
gh repo create my-site --private --source=. --push
```

Verify:

```bash
git remote -v
gh repo view --web
```

## Step 4 — Start Claude Code in the project

```bash
claude
```

Sign in on first run. Then inside Claude Code, run `/init` to generate `CLAUDE.md` — trim it to \~20–40 lines covering stack, build/test commands, and conventions (it's loaded every session, so every line costs tokens).

First task to try: "Build a simple static site with Vite and plain HTML/CSS, then add a GitHub Actions workflow that deploys it to GitHub Pages." Review the diff, then commit and push.

## Habits that keep token use down

| Habit | Why it helps |
| --- | --- |
| `/clear` between unrelated tasks | Old conversation history is the biggest hidden cost |
| `/compact` during long tasks | Replaces history with a short summary |
| Name files with `@path/file` | Stops Claude exploring the whole repo |
| Plan mode (Shift+Tab) before big changes | Avoids expensive rewrites from misalignment |
| Switch model with `/model` (Sonnet/Haiku for routine edits) | Save Opus-tier usage for design-heavy work |
| One small, specific task per prompt | Less context, fewer retries |
| Keep `CLAUDE.md` short and accurate | It's loaded every session |
| Check `/cost` or `/usage` periodically | Shows which habits are expensive |

## Roadmap — toward an SDD-driven MCP system

1. **Stage 2 — build a small MCP server** (Java: Spring AI's MCP server starter). Expose a single read-only tool: `getServiceContext(name)` for curated context about the target system. Spec management is handled by GitHub Spec Kit (installed separately). Connect with `claude mcp add` and observe when Claude calls it.
2. **Stage 2.5 — Spec Kit workflow in Claude Code.** Install GitHub Spec Kit (`specify init my-site --integration claude`), run the constitution, and use the Spec Kit slash commands (`/speckit.specify`, `/speckit.implement`, `/speckit.converge`) to drive the spec → plan → code → test → PR cycle, with subagents for review/testing.
3. **Stage 3 — run it unattended.** Move to the Claude Agent SDK (needs an API key) or run Claude Code from GitHub Actions: a merged spec triggers an agent, which calls the MCP server for context on the existing system and opens a PR.
4. **Stage 4 — point it at a real existing codebase.** Give the agent focused tools (module map, service API contracts) rather than letting it read everything, since context management matters most at this scale.

## Migrating to Spec Kit — Claude instructions

When Claude Code is asked to replace the custom spec store with GitHub Spec Kit, follow these steps in order:

1. **Install Spec Kit.** Run:

```bash
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git
```

If `uv` is not installed, install it first: `curl -LsSf https://astral.sh/uv/install.sh | sh`

2. **Initialise Spec Kit for Claude Code.** In the target repo (e.g. `my-site`):

```bash
specify init my-site --integration claude
```

This installs skills into `.claude/skills/` and registers the Spec Kit slash commands.

3. **Strip the spec tools from the MCP server.** In `sdd-mcp-server/src/main/java/dev/amr/sddmcp/SpecTools.java`:
   - Delete the `listSpecs` method and the `SpecSummary` record.
   - Keep (or add) a `getServiceContext(String name)` tool that reads `context/<name>.md` and returns the file contents. This is the server's only remaining tool.
4. **Run the constitution.** Inside Claude Code, run `/speckit.constitution` to generate project-specific rules that guide the agent.
5. **Create the first spec.** Run `/speckit.specify` and follow the prompts. The full Spec Kit workflow from here is: `constitution → specify → clarify → plan → checklist → tasks → implement → converge`.

After these steps, spec management lives entirely in Spec Kit's slash commands, and the MCP server is a lightweight context provider only.

## Step 5 — Deploy to GitHub Pages

If the site deploys via a GitHub Actions workflow (`.github/workflows/deploy.yml`), GitHub Pages still needs to be turned on once before that workflow can publish anything.

1. Push your changes: `git add . && git commit -m "..." && git push`
2. Repo → **Settings** → **Pages** → under "Build and deployment," set **Source** to **GitHub Actions**
3. Repo → **Actions** tab → confirm the workflow run is green. If it failed, open it to see which step failed
4. Once it succeeds, the site URL appears at the top of the Pages settings page, e.g. `https://<username>.github.io/<repo>/`

## Troubleshooting log

Issues hit during setup/deploy and how they were resolved, in case they recur.

| Issue | Cause | Fix |
| --- | --- | --- |
| `zsh: command not found: claude` after install | `~/.local/bin` (where the native installer puts the binary) wasn't on PATH | `echo 'export PATH="$HOME/.local/bin:$PATH"' >> ~/.zshrc && source ~/.zshrc` |
| `gh repo create --push` failed: "no commits found" | Tried to create+push before any commit existed in the folder | Created an initial commit (`git add` + `git commit`) first, then ran `gh repo create ... --push` |
| `zsh: permission denied: .github/workflows/deploy.yml` | Typed the file path directly instead of a command — shell tried to execute the YAML file | Used `cat .github/workflows/deploy.yml` to view it instead |
| `actions/configure-pages@v5` step failed: "Get Pages site failed... Not Found" | GitHub Pages had never been enabled for the repo | Went to Settings → Pages → set Source to **GitHub Actions** (one-time manual step), then re-ran the workflow |
| Pages settings showed "Upgrade or make this repository public to enable Pages" | Repo was created `--private`, and Pages requires a public repo (or GitHub Enterprise) to build via Actions on the free plan | Made the repo public: `gh repo edit <owner>/<repo> --visibility public`, then re-checked Settings → Pages and re-ran the workflow |
| Build error: package org.springaicommunity.mcp.annotation does not exist (MCP server, @McpTool imports) | Imports were taken from the pre-2.0 standalone annotations library; Spring AI 2.0 ships the MCP annotations inside Spring AI under a different package | Looked up the real package inside the spring-ai-mcp-annotations jar in \~/.m2 (unzip -l, grep McpTool) and updated the imports to match |
| Logback line "ERROR ... Empty or null pattern" printed before the JSON reply when smoke-testing the stdio server | Setting logging.pattern.console to an empty string made Logback print its own status message to stdout, which is the MCP protocol channel | Removed the logging block from application.yml; added logback-spring.xml with a file appender only plus a NopStatusListener. Stdout then carried only JSON-RPC lines |
