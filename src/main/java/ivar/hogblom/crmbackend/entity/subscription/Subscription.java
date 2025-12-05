package ivar.hogblom.crmbackend.entity.subscription;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME) // UUIDv7
    private UUID id;

    // ✔ Required-fält
    @Column(nullable = false, unique = true)
    private String name;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "service_level")
    private String serviceLevel;

    // ✔ Required-fält
    @Column(name = "price_per_month", nullable = false)
    private Double pricePerMonth;

    // ✔ Required-fält
    @Column(name = "contract_length", nullable = false)
    private Integer contractLength;

    @Column(name = "renewal_period")
    private Integer renewalPeriod;

    private Boolean active;

    @Column(name = "support_contact")
    private String supportContact;

    @Column(name = "created_at")
    private LocalDate createdAt;

    private String notes;
}
