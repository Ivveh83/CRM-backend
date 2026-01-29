package ivar.hogblom.crmbackend.system.entity.db;

import ivar.hogblom.crmbackend.system.entity.userEntityAndRole.UserEntity;
import ivar.hogblom.crmbackend.system.service.db.security.EncryptedStringConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "database_connections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatabaseConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // t.ex. "postgres", "mysql", "sqlite", "sqlcipher"
    @Column(nullable = false)
    private String type;

    private String host;
    private Integer port;
    private String databaseName;
    @Convert(converter = EncryptedStringConverter.class)
    private String username;
    @Convert(converter = EncryptedStringConverter.class)
    private String password;
    private String filePath;
//    private String encryptionKey;

    // ägare - koppling till din UserEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    //Om flera användare ska kunna dela samma db
    /*
    @ManyToMany
    List<UserEntity> allowedUsers;
    */
}
