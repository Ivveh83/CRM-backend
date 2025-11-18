package ivar.hogblom.crmbackend.service;


import ivar.hogblom.crmbackend.dto.AuthRequestDto;
import ivar.hogblom.crmbackend.dto.AuthResponseDto;

public interface AuthService {

    AuthResponseDto login(AuthRequestDto request);

    void logout(String authHeader);
}