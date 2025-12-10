package ivar.hogblom.crmbackend.crm.entity.contract;

import ivar.hogblom.crmbackend.config.jpa.LocalDateTimeEpochMillisConverter;
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

    @Column(name = "contract_id", nullable = false)
    private UUID contractId;

    @Column(name = "customer_org_no")
    private String customerOrgNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private ContractEventType eventType;

    @Column(name = "event_ts", nullable = false)
    @Convert(converter = LocalDateTimeEpochMillisConverter.class)
    private LocalDateTime eventTs;

    @Column(columnDefinition = "TEXT")
    private String detail;

    private String actor; // användarnamn från auth
}
