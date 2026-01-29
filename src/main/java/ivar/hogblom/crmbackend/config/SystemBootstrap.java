package ivar.hogblom.crmbackend.config;

import ivar.hogblom.crmbackend.system.entity.userEntityAndRole.Role;
import ivar.hogblom.crmbackend.system.entity.userEntityAndRole.UserEntity;
import ivar.hogblom.crmbackend.system.repository.userEntityAndRole.RoleRepository;
import ivar.hogblom.crmbackend.system.repository.userEntityAndRole.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SystemBootstrap implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserEntityRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(transactionManager = "systemTransactionManager")
    public void run(String... args) {

        // -------------------------
        // Roles
        // -------------------------
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        // -------------------------
        // Admin user
        // -------------------------
        if (userRepository.findByUsername("admin").isEmpty()) {

            UserEntity admin = UserEntity.builder()
                    .username("admin")
                    .email("admin@system.local")
                    .password(passwordEncoder.encode("password")) // byt direkt i prod
                    .roles(List.of(adminRole, userRole))
                    .createdAt(LocalDate.now())
                    .build();

            userRepository.save(admin);

            System.out.println("✅ Default admin user created");
        }
    }
}
