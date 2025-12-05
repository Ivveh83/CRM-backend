package ivar.hogblom.crmbackend.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Event information for a customer")
public record CustomerEventDto(

        @Schema(description = "Event ID")
        UUID id,

        @Schema(description = "Type of event", example = "SKAPAT")
        String eventType,

        @Schema(description = "Event detail text")
        String detail,

        @Schema(description = "Timestamp of the event")
        LocalDateTime eventTs,

        @Schema(description = "Actor performing the event")
        String actor

) {}
