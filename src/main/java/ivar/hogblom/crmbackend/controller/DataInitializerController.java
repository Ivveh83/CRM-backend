package ivar.hogblom.crmbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import ivar.hogblom.crmbackend.ai.initializer.DemoDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/data-initializer")
@Profile({"dev", "test"})
public class DataInitializerController {

    private final DemoDataService demoDataService;
    private final boolean enabled;

    public DataInitializerController(
            DemoDataService demoDataService,
            @Value("${data-initializer.enabled:false}") boolean enabled
    ) {
        this.demoDataService = demoDataService;
        this.enabled = enabled;
    }

    // -----------------------------------------------------
    // 🔵 GENERATE DEMO DATA
    // -----------------------------------------------------
    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Generate demo/test data")
    @PreAuthorize("hasRole('ADMIN')")
    public void generateDemoData() {

        if (!enabled) {
            throw new IllegalStateException("Data initializer is disabled");
        }

        demoDataService.generateDemoData();
    }
}

