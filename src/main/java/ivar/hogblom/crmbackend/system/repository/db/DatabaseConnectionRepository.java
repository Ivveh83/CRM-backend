package ivar.hogblom.crmbackend.system.repository.db;



import ivar.hogblom.crmbackend.system.entity.db.DatabaseConnection;
import ivar.hogblom.crmbackend.system.entity.userEntityAndRole.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DatabaseConnectionRepository extends JpaRepository<DatabaseConnection, Long> {
    List<DatabaseConnection> findByOwner(UserEntity owner);
    List<DatabaseConnection> findByOwnerUsername(String userName);
    Optional<DatabaseConnection> findByIdAndOwner(UUID id, UserEntity owner);
    Optional<DatabaseConnection> findById(UUID id);
    boolean existsByOwnerId(UUID id);
}
