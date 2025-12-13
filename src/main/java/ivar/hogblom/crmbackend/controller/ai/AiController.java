package ivar.hogblom.crmbackend.controller.ai;


import ivar.hogblom.crmbackend.ai.DynamicChatService;
import ivar.hogblom.crmbackend.dto.ai.DynamicAiRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final DynamicChatService dynamicChatService;

    public AiController(DynamicChatService dynamicChatService) {
        this.dynamicChatService = dynamicChatService;
    }

    @Operation(
            summary = "CRM AI Assistant",
            description = """
            Interact with the CRM AI assistant.

            The behavior depends on the selected systemPromptProfile:
            - analysis: Read-only analysis of contracts, events, customers
            - decision: Decision support (prioritization, next steps)
            - communication: Draft customer emails
            - action: Perform contract actions (CUD) via tools

            Responses are streamed.
            """
    )
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved AI response stream")
    @PostMapping("/chat")
    @ResponseStatus(HttpStatus.OK)
    public Flux<String> chat(
            @RequestBody DynamicAiRequest request
    ) {
        return dynamicChatService.chat(request);
    }
}
