package ivar.hogblom.crmbackend.crm.entity.customer;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "customer_event")
public class CustomerEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerEventType eventType;

    @Column(nullable = false)
    private LocalDateTime eventTs;

    @Column(columnDefinition = "TEXT")
    private String detail;

    private String actor;  // användarnamn från auth
}
