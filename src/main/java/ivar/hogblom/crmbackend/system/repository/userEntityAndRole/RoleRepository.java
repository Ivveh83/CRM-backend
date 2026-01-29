package ivar.hogblom.crmbackend.system.repository.userEntityAndRole;

import ivar.hogblom.crmbackend.system.entity.userEntityAndRole.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);

    boolean existsByName(String name);
}
