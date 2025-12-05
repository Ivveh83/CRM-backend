package ivar.hogblom.crmbackend.entity.lookup;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lookup_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LookupValue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String type; // ex: "subscription_category", "sla_level"

    @Column(nullable = false, unique = false)
    private String value; // ex: "threat_monitoring", "bronze", alltså en slugg av label

    @Column(nullable = false)
    private String label;

    private Integer sortOrder;

    @Column(nullable = false)
    private boolean active;
}

