package dev.amr.sddmcp;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ContextController {

    private final SpecTools specTools;

    public ContextController(SpecTools specTools) {
        this.specTools = specTools;
    }

    @GetMapping(value = "/context", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getContext(@RequestParam String service) {
        return specTools.getServiceContext(service);
    }
}
