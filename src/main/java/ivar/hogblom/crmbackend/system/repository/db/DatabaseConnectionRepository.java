package ivar.hogblom.crmbackend.system.repository.db;



import ivar.hogblom.crmbackend.system.entity.db.DatabaseConnection;
import ivar.hogblom.crmbackend.system.entity.userEntityAndRole.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DatabaseConnectionRepository extends JpaRepository<DatabaseConnection, Long> {
    List<DatabaseConnection> findByOwner(UserEntity owner);
    Optional<DatabaseConnection> findByIdAndOwner(UUID id, UserEntity owner);
}
