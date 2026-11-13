package dev.amr.sddmcp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpecTools {

    private final Path contextRoot;

    public SpecTools(@Value("${sdd.context-root:../my-site/context}") String contextRoot) {
        this.contextRoot = Paths.get(contextRoot).toAbsolutePath().normalize();
    }

    @McpTool(name = "getServiceContext",
             description = "ALWAYS call this first when working on a spec. "
                         + "Returns a curated summary of the named service: repository layout, "
                         + "key files to edit, public API surface, build and test commands, and "
                         + "design decisions that constrain every change. "
                         + "This is faster and cheaper than reading individual files, and it tells you "
                         + "exactly which files are relevant so you do not read ones that are not. "
                         + "Pass the value of the spec's 'service' front-matter field as `name`. "
                         + "Do not read any file in the service before calling this.")
    public String getServiceContext(
            @McpToolParam(description = "Service name, e.g. my-site") String name) {
        Path file = contextRoot.resolve(name + ".md").normalize();
        if (!file.startsWith(contextRoot)) {
            return "ERROR: invalid service name";
        }
        if (!Files.exists(file)) {
            return "No context found for service '" + name + "'. "
                 + "Expected file: " + file;
        }
        try {
            return Files.readString(file);
        } catch (IOException e) {
            return "ERROR reading context file: " + e.getMessage();
        }
    }
}
