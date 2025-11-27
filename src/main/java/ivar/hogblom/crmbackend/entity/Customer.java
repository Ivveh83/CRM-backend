package ivar.hogblom.crmbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME) // UUIDv7
    private UUID id;

    // ⬅ ENDA required-fältet
    @Column(name = "company_name", nullable = false, unique = true)
    private String companyName;

    @Column(name = "org_no", unique = true)
    private String orgNo;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    private String address;

    private String city;

    @Column(name = "zip_code")
    private String zipCode;

    private String country;

    private String industry;

    @Column(name = "customer_type")
    private String customerType;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "customer")
    private List<Contract> contracts = new ArrayList<>();

}
