package ivar.hogblom.crmbackend.crm.entity.reseller;

import ivar.hogblom.crmbackend.config.jpa.LocalDateEpochMillisConverter;
import ivar.hogblom.crmbackend.system.service.db.security.EncryptedStringConverter;
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
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
@Table(name = "resellers")
public class Reseller {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME) // UUIDv7
    private UUID id;

    // ✔ Required-fält
    @Column(nullable = false, unique = true)
    private String name;

    // ✔ Required-fält
    @Column(name = "org_no", nullable = false, unique = true)
    private String orgNo;

    @Column(nullable = false)
    private boolean active;


    @Convert(converter = EncryptedStringConverter.class)
    private String address;

    @Column(name = "contact_email")
    @Convert(converter = EncryptedStringConverter.class)
    private String contactEmail;

    @Column(name = "contact_telephone")
    @Convert(converter = EncryptedStringConverter.class)
    private String contactTelephone;

    @Column(name = "invoice_reference")
    @Convert(converter = EncryptedStringConverter.class)
    private String invoiceReference;

    @Column(name = "created_at")
    @Convert(converter = LocalDateEpochMillisConverter.class)
    private LocalDate createdAt;

}
