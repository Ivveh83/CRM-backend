package ivar.hogblom.crmbackend.service.role;

import ivar.hogblom.crmbackend.dto.role.RoleRequestDto;
import ivar.hogblom.crmbackend.dto.role.RoleResponseDto;
import ivar.hogblom.crmbackend.entity.userEntityAndRole.Role;
import ivar.hogblom.crmbackend.repository.userEntityAndRole.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;


    // ---------------------------------------------------------
    // GET ALL ROLES
    // ---------------------------------------------------------
    @Override
    public List<RoleResponseDto> getAllRoles() {
        List<Role> roles = roleRepository.findAll();

        return roles.stream().map(this::toResponseDto).toList();
    }

    // ---------------------------------------------------------
    // CREATE ROLE
    // ---------------------------------------------------------
    @Transactional
    @Override
    public void createRole(RoleRequestDto dto) {
        if (dto == null || dto.name().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }
        roleRepository.save(new Role(dto.name()));
    }

    // ---------------------------------------------------------
    // DELETE ROLE
    // ---------------------------------------------------------
    public void deleteRole(UUID roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        if (!role.getUsers().isEmpty()) {
            throw new IllegalStateException("Role is in use from users and cannot be deleted");
        }

        roleRepository.delete(role);
    }


    private RoleResponseDto toResponseDto(Role role) {
        return RoleResponseDto.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}
