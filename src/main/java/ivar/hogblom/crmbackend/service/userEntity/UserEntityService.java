package ivar.hogblom.crmbackend.service.userEntity;

import ivar.hogblom.crmbackend.dto.userEntity.*;

import java.util.List;
import java.util.UUID;

public interface UserEntityService {

    void create(UserEntityRegistrationDto dto);

    List<UserEntityDto> getAllUsers();

    UserEntityDto getUser(UUID id);

    void updateUser(UserEntityDto dto);

    void addRoleToUser(AddRoleToUserDto dto);

    void removeRoleFromUser(RemoveRoleFromUserDto dto);

    void changePassword(ChangePasswordRequestDto dto);

    void deleteUser(UUID id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void resetPasswordAndExport(String username);
}
