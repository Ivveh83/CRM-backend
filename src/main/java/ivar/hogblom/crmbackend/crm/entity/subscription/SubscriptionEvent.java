package ivar.hogblom.crmbackend.crm.entity.subscription;

import ivar.hogblom.crmbackend.config.jpa.LocalDateTimeEpochMillisConverter;
import ivar.hogblom.crmbackend.system.service.db.security.EncryptedStringConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "subscription_event")
public class SubscriptionEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "subscription_id", nullable = false)
    private UUID subscriptionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private SubscriptionEventType eventType;

    @Column(name = "event_ts", nullable = false)
    @Convert(converter = LocalDateTimeEpochMillisConverter.class)
    private LocalDateTime eventTs;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = EncryptedStringConverter.class)
    private String detail;

    private String actor;
}
