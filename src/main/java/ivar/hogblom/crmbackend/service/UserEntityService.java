package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.ChangePasswordRequestDto;
import ivar.hogblom.crmbackend.dto.UserEntityDto;
import ivar.hogblom.crmbackend.dto.UserEntityRegistrationDto;

public interface UserEntityService {

    UserEntityDto create(UserEntityRegistrationDto userEntityRegistrationDto);
    void changePassword(String username, ChangePasswordRequestDto changePasswordRequestDto);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
