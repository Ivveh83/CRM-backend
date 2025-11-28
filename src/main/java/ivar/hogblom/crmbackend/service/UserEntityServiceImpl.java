package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.ChangePasswordRequestDto;
import ivar.hogblom.crmbackend.dto.UserEntityDto;
import ivar.hogblom.crmbackend.dto.UserEntityRegistrationDto;
import ivar.hogblom.crmbackend.entity.Role;
import ivar.hogblom.crmbackend.entity.UserEntity;
import ivar.hogblom.crmbackend.repository.RoleRepository;
import ivar.hogblom.crmbackend.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserEntityServiceImpl implements UserEntityService {

    private final RoleRepository roleRepository;
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserEntityServiceImpl(
            UserEntityRepository userEntityRepository,
            PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserEntityDto create(UserEntityRegistrationDto userEntityRegistrationDto) {

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

        Role role = roleRepository.findByName("ROLE_ADMIN").get();
        userEntity.setRoles(List.of(role));
        userEntityRepository.save(userEntity);

        return convertToDto(userEntity);
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequestDto dto) {

        System.out.println("Changing password for username: " + username);
        UserEntity existingUser = userEntityRepository.findByUsername(username)
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
}
