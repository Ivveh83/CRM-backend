package ivar.hogblom.crmbackend.system.service.userEntity;

import ivar.hogblom.crmbackend.dto.userEntity.*;
import ivar.hogblom.crmbackend.system.entity.userEntityAndRole.Role;
import ivar.hogblom.crmbackend.system.entity.userEntityAndRole.UserEntity;
import ivar.hogblom.crmbackend.system.repository.db.DatabaseConnectionRepository;
import ivar.hogblom.crmbackend.system.repository.userEntityAndRole.RoleRepository;
import ivar.hogblom.crmbackend.system.repository.userEntityAndRole.UserEntityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(transactionManager = "systemTransactionManager")
public class UserEntityServiceImpl implements UserEntityService {

    private final RoleRepository roleRepository;
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final DatabaseConnectionRepository databaseConnectionRepository;

    @Autowired
    public UserEntityServiceImpl(
            UserEntityRepository userEntityRepository,
            PasswordEncoder passwordEncoder, RoleRepository roleRepository, DatabaseConnectionRepository databaseConnectionRepository) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.databaseConnectionRepository = databaseConnectionRepository;
    }

    @Override
    public void create(UserEntityRegistrationDto userEntityRegistrationDto) {

        if (existsByUsername(userEntityRegistrationDto.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (existsByEmail(userEntityRegistrationDto.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userEntityRegistrationDto.username());
        userEntity.setEmail(userEntityRegistrationDto.email());
        userEntity.setPassword(passwordEncoder.encode(userEntityRegistrationDto.password()));

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER saknas i system-databasen"));
        userEntity.setRoles(List.of(role));
        userEntityRepository.save(userEntity);
    }

    @Override
    public void changePassword(ChangePasswordRequestDto dto) {

        UserEntity existingUser = userEntityRepository.findByUsername(dto.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.currentPassword(), existingUser.getPassword())) {
            throw new RuntimeException("Incorrect current password");
        }

        if (dto.currentPassword().equals(dto.newPassword())) {
            throw new RuntimeException("New password must be different from current password");
        }

        existingUser.setPassword(passwordEncoder.encode(dto.newPassword()));
        userEntityRepository.save(existingUser);
    }
    // ---------------------------------------------------------
    // GET ALL USERS
    // ---------------------------------------------------------
    @Override
    public List<UserEntityDto> getAllUsers() {
        return userEntityRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }


    // ---------------------------------------------------------
    // GET USER
    // ---------------------------------------------------------
    @Override
    public UserEntityDto getUser(UUID id) {
        UserEntity user = userEntityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return toDto(user);
    }

    // ---------------------------------------------------------
    // UPDATE USER
    // ---------------------------------------------------------
    @Override
    public void updateUser(UserEntityDto dto) {
        UserEntity existingUser = userEntityRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!existingUser.getUsername().equals(dto.username())) {
            if (existsByUsername(dto.username())) {
                throw new IllegalArgumentException("Username already exists");
            }
            existingUser.setUsername(dto.username());
        }

        if (!existingUser.getEmail().equals(dto.email())) {
            if (existsByEmail(dto.email())) {
                throw new IllegalArgumentException("Email already exists");
            }
            existingUser.setEmail(dto.email());
        }

        existingUser.setUsername(dto.username());
        existingUser.setEmail(dto.email());

        userEntityRepository.save(existingUser);
    }

    // ---------------------------------------------------------
    // ADD ROLE
    // ---------------------------------------------------------
    @Override
    public void addRoleToUser(AddRoleToUserDto dto) {
        UserEntity user = userEntityRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        System.out.println("Role : " + dto.roleName());
        Role role = roleRepository.findByName(dto.roleName())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
        }else {
            throw new RuntimeException("Role already exists for user");
        }

        userEntityRepository.save(user);
    }

    public void removeRoleFromUser(RemoveRoleFromUserDto dto) {
        UserEntity user = userEntityRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // --- Prevent ADMIN from removing their own ADMIN role ---
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        boolean isSelf = user.getUsername().equals(currentUsername);
        boolean isRemovingAdminRole = dto.roleName().equalsIgnoreCase("ROLE_ADMIN");

        if (isSelf && isRemovingAdminRole) {
            throw new IllegalArgumentException("Not allowed to remove your own admin role");
        }

        // --- Continue with normal role removal ---
        Role role = roleRepository.findByName(dto.roleName())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        user.getRoles().remove(role);
        userEntityRepository.save(user);
    }

    // ---------------------------------------------------------
    // DELETE USER
    // ---------------------------------------------------------
    @Override
    public void deleteUser(UUID id) {
        if (!userEntityRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }
        if (databaseConnectionRepository.existsByOwnerId(id)) {
            throw new IllegalStateException(
                    "User owns database connections and cannot be deleted"
            );
        }

        userEntityRepository.deleteById(id);
    }

    // ---------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------

    @Override
    public boolean existsByUsername(String username) {
        return userEntityRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userEntityRepository.existsByEmail(email);
    }

    private UserEntityDto convertToDto(UserEntity userEntity) {
        return UserEntityDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .build();
    }

    private UserEntityDto toDto(UserEntity user) {
        return UserEntityDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .toList()
                )
                .build();
    }
}

