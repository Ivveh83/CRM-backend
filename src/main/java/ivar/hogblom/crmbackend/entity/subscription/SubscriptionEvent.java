package ivar.hogblom.crmbackend.entity.subscription;

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

    @Column(nullable = false)
    private UUID subscriptionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionEventType eventType;

    @Column(nullable = false)
    private LocalDateTime eventTs;

    @Column(columnDefinition = "TEXT")
    private String detail;

    private String actor;
}
