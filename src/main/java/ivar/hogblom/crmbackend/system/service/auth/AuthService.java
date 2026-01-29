package ivar.hogblom.crmbackend.system.service.auth;


import ivar.hogblom.crmbackend.dto.auth.AuthRequestDto;
import ivar.hogblom.crmbackend.dto.auth.AuthResponseDto;

public interface AuthService {

    AuthResponseDto login(AuthRequestDto request);

    void logout(String authHeader);
}