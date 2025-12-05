package ivar.hogblom.crmbackend.dto.reseller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Event information for a reseller")
public record ResellerEventDto(

        @Schema(description = "Event ID")
        UUID id,

        @Schema(description = "Type of event", example = "SKAPAT")
        String eventType,

        @Schema(description = "Event detail text")
        String detail,

        @Schema(description = "Timestamp of event")
        LocalDateTime eventTs,

        @Schema(description = "Actor performing the event")
        String actor

) {}
