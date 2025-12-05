package ivar.hogblom.crmbackend.entity.contract;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "contract_event")
public class ContractEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID contractId;

    private String customerOrgNo;   // ⭐ nytt fält

    @Enumerated(EnumType.STRING)
    private ContractEventType eventType;

    @Column(nullable = false)
    private LocalDateTime eventTs;

    @Column(columnDefinition = "TEXT")
    private String detail;

    private String actor; // användarnamn från auth
}
