package ivar.hogblom.crmbackend.system.service.auth;

import ivar.hogblom.crmbackend.dto.auth.AuthRequestDto;
import ivar.hogblom.crmbackend.dto.auth.AuthResponseDto;
import ivar.hogblom.crmbackend.system.entity.userEntityAndRole.UserEntity;
import ivar.hogblom.crmbackend.system.repository.userEntityAndRole.UserEntityRepository;
import ivar.hogblom.crmbackend.security.JwtTokenUtil;
import ivar.hogblom.crmbackend.security.TokenBlacklistStorage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional(transactionManager = "systemTransactionManager")
public class AuthServiceImpl implements AuthService {

    private final UserEntityRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenBlacklistStorage tokenBlacklistStorage;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenUtil jwtTokenUtil,
                           TokenBlacklistStorage tokenBlacklistStorage,
                           UserEntityRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenBlacklistStorage = tokenBlacklistStorage;
        this.userRepository = userRepository;
    }


    @Override
    public AuthResponseDto login(AuthRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtTokenUtil.generateToken(userDetails, null);

        UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return AuthResponseDto.builder()
                .token(jwt)
                .type("Bearer")
                .username(userDetails.getUsername())
                .email(userEntity.getEmail())
                .roles(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray(String[]::new))
                .build();
    }

    @Override
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        String token = authHeader.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);

        try {
            if (tokenBlacklistStorage.isBlacklisted(token)) {
                throw new IllegalArgumentException("Token has already been invalidated");
            }

            Date expiryDate = jwtTokenUtil.getExpirationDateFromToken(token);
            tokenBlacklistStorage.blacklistToken(token, username, expiryDate.toInstant());
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token: " + e.getMessage());
        }
    }
}