package ivar.hogblom.crmbackend.crm.entity.contract;

import ivar.hogblom.crmbackend.config.jpa.LocalDateEpochMillisConverter;
import ivar.hogblom.crmbackend.crm.entity.customer.Customer;
import ivar.hogblom.crmbackend.crm.entity.reseller.Reseller;
import ivar.hogblom.crmbackend.crm.entity.subscription.Subscription;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"customer", "resellers", "subscriptions"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToMany
    @JoinTable(
            name = "contract_resellers",
            joinColumns = @JoinColumn(name = "contract_id"),
            inverseJoinColumns = @JoinColumn(name = "reseller_id")
    )
    private List<Reseller> resellers;

    @ManyToMany
    @JoinTable(
            name = "contract_subscriptions",
            joinColumns = @JoinColumn(name = "contract_id"),
            inverseJoinColumns = @JoinColumn(name = "subscription_id")
    )
    private List<Subscription> subscriptions;

    @Column(nullable = false)
    private boolean status;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "contract_date", nullable = false)
    @Convert(converter = LocalDateEpochMillisConverter.class)
    private LocalDate contractDate;

    @Column(name = "contract_length_months", nullable = false)
    private Integer contractLengthMonths;


    @Column(name = "total_price_per_month", nullable = false)
    private Double totalPricePerMonth;

    @Column(name = "due_date", nullable = false)
    @Convert(converter = LocalDateEpochMillisConverter.class)
    private LocalDate dueDate;

    @ElementCollection
    @CollectionTable(
            name = "contract_renewal_dates",
            joinColumns = @JoinColumn(name = "contract_id")
    )
    @Column(name = "renewal_dates")
    @Convert(converter = LocalDateEpochMillisConverter.class)
    private List<LocalDate> renewalDates;

    @Column(columnDefinition = "TEXT")
    private String comment;
}
