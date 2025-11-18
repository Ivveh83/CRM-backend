package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.UserEntityDto;
import ivar.hogblom.crmbackend.dto.UserEntityRegistrationDto;

public interface UserEntityService {

    UserEntityDto create(UserEntityRegistrationDto userEntityRegistrationDto);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
