package ivar.hogblom.crmbackend.service.role;

import ivar.hogblom.crmbackend.dto.role.RoleRequestDto;
import ivar.hogblom.crmbackend.dto.role.RoleResponseDto;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    List<RoleResponseDto> getAllRoles();
    void createRole(RoleRequestDto dto);
    void deleteRole(UUID id);
}
