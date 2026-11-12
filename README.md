# Claude Commands

List MCP tools
```
 /mcp
```

Restart the MCP server so Claude Code picks up the new jar and new description:
```
 claude mcp restart sdd-specs
```

---
# Issues

1- don't call our mcp first before going to claude
```
update code of custom mcp tool[getServiceContext] (ALWAYS call this first when working on a spec.)
```
