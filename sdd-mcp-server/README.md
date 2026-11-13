# sdd-mcp-server

MCP server that delivers curated context about the target service to Claude Code. Part of the Spec-Driven Development (SDD) agent project.

## What it does

Exposes a single read-only tool — `getServiceContext` — that returns the content of a hand-written context file for a named service. The agent calls this tool before touching any file in a service, so it knows the module map, public API, build commands, and key design decisions without reading the whole repository.

## Requirements

- Java 26 (or 25 LTS)
- Maven 3.9+

## Build

```bash
cd sdd-mcp-server
mvn package -q
```

The fat jar lands at `target/sdd-mcp-server-0.1.0.jar`.

## Run (stdio, for local use)

```bash
java -jar target/sdd-mcp-server-0.1.0.jar
```

The server speaks JSON-RPC over stdin/stdout. Do not run it in a terminal directly — Claude Code manages the process.

Logs go to `~/sdd-mcp-server.log`. Nothing is written to stdout except the protocol.

## Register with Claude Code

```bash
claude mcp add sdd-specs --scope user -- \
  java -jar /path/to/sdd-mcp-server/target/sdd-mcp-server-0.1.0.jar
```

Verify:

```
claude mcp list
# sdd-specs   ✔ Connected
```

## Context root (where service context files live)

By default the server looks for context files in `../my-site/context` relative to the working directory when it starts. Override this with an environment variable:

```bash
claude mcp add sdd-specs --scope user \
  -e SDD_CONTEXT_ROOT=/absolute/path/to/context \
  -- java -jar /path/to/target/sdd-mcp-server-0.1.0.jar
```

Or add to `application.yml`:

```yaml
sdd:
  context-root: /absolute/path/to/context
```

## Context file format

One markdown file per service, at `<context-root>/<service-name>.md`. The `service` field in a spec's front matter is the lookup key.

Example: a spec with `service: my-site` causes the agent to call `getServiceContext("my-site")`, which reads `<context-root>/my-site.md`.

## Troubleshooting

| Symptom | Cause | Fix |
| --- | --- | --- |
| `sdd-specs ✗ Failed` in `claude mcp list` | Jar not built or wrong path | Run `mvn package` and confirm the path in `claude mcp list` exists |
| `No context found for service 'X'` | Missing context file | Create `<context-root>/X.md` |
| Agent never calls `getServiceContext` | Tool description not matching the agent's intent | See T-13 — revise the description and re-run the discovery test |
| Blank log file | Server never started | Check the jar path; run it manually and look at stderr |
| Stray output on stdout | A log appender or banner writing to stdout | Check `logback-spring.xml` and `spring.main.banner-mode: off` in `application.yml` |

## Tests

```bash
mvn test
```

Three unit tests cover: file found, file missing, and path traversal rejection.
