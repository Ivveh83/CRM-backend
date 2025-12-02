package ivar.hogblom.crmbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "reseller_event")
public class ResellerEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID resellerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResellerEventType eventType;

    @Column(nullable = false)
    private LocalDateTime eventTs;

    @Column(columnDefinition = "TEXT")
    private String detail;

    private String actor;
}
