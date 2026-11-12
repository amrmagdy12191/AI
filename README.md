# Claude Commands

List MCP tools
```
 /mcp
```

Restart the MCP server so Claude Code picks up the new jar and new description:
```
To use it: start the server with java -jar target/sdd-mcp-server-0.1.0.jar
```
```
 claude mcp restart sdd-specs
```


---
# Issues

1- don't call our mcp first before going to claude
```
update code of custom mcp tool[getServiceContext] (ALWAYS call this first when working on a spec.)
```
