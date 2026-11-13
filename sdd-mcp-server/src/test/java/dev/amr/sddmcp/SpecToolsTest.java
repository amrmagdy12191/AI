package dev.amr.sddmcp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class SpecToolsTest {

    @TempDir
    Path tempDir;

    @Test
    void returnsContextWhenFileExists() throws IOException {
        Files.writeString(tempDir.resolve("my-service.md"), "# Service context");
        SpecTools tools = new SpecTools(tempDir.toString());

        String result = tools.getServiceContext("my-service");

        assertThat(result).isEqualTo("# Service context");
    }

    @Test
    void returnsHelpfulMessageWhenFileIsMissing() {
        SpecTools tools = new SpecTools(tempDir.toString());

        String result = tools.getServiceContext("nonexistent");

        assertThat(result).startsWith("No context found for service 'nonexistent'");
    }

    @Test
    void rejectsPathTraversal() {
        SpecTools tools = new SpecTools(tempDir.toString());

        String result = tools.getServiceContext("../../etc/passwd");

        assertThat(result).startsWith("ERROR: invalid service name");
    }
}
