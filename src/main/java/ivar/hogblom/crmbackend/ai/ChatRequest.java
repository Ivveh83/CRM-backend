package ivar.hogblom.crmbackend.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
@Schema(description = "Dynamic AI request supporting multiple providers, roles, and models")
public record ChatRequest(

    @Schema(description = "Optional model override. If null, provider default is used",
            example = "gpt-4.1-mini")
    String model,

    @NotEmpty
    @Schema(description = "User prompt sent to the AI",
            example = "Analyze the contract activity for customer X")
    String prompt,

    @NotEmpty
    @Pattern(regexp = "ANALYSIS|DECISION_SUPPORT|EMAIL_CREATION|ACTION")
    @Schema(description = "System prompt profile / role",
            allowableValues = {"ANALYSIS", "DECISION_SUPPORT", "EMAIL_CREATION", "ACTION"})
    String systemPromptProfile,

    @Schema(description = "Conversation ID for shared chat memory",
            example = "conv-123")
    String conversationId,

    @NotNull
    String crmDatabaseId,

    @Schema(description = "Optional temperature override", example = "0.3")
    Double temperature,

    @Schema(description = "Optional max token override", example = "800")
    Integer maxTokens,

    @Schema(description = "Optional top-p override", example = "0.95")
    Double topP
            )
    {}
