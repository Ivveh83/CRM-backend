package ivar.hogblom.crmbackend.service;

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
    UserEntityRepository userEntityRepository;
    PasswordEncoder passwordEncoder;

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

        Role role = roleRepository.findByName("ADMIN").get();
        userEntity.setRoles(List.of(role));
        userEntityRepository.save(userEntity);

        return convertToDto(userEntity);
    }

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    private UserEntityDto convertToDto(UserEntity userEntity) {
        return UserEntityDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .build();
    }
}
