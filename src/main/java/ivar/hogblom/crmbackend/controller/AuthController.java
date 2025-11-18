package ivar.hogblom.crmbackend.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import ivar.hogblom.crmbackend.dto.AuthRequestDto;
import ivar.hogblom.crmbackend.dto.AuthResponseDto;
import ivar.hogblom.crmbackend.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @SecurityRequirements
    @Operation(summary = "Login to get JWT token",
            description = "Authenticate user credentials and return JWT token")
    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody AuthRequestDto request) {
        return authService.login(request);
    }

    @Operation(summary = "Logout and revoke token",
            description = "Invalidates the current JWT token")
    @ApiResponse(responseCode = "200", description = "Successfully logged out")
    @ApiResponse(responseCode = "400", description = "Invalid token or no token provided")
    @PostMapping("/logout")
    public void logout(@NotNull(message = "Authorization header cannot be null")
                           @RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
    }


    //För HttpOnly jwt-cookie
    /*
    Du måste skicka tillbaka jwt i en HttpOnly-cookie som detta:

ResponseCookie cookie = ResponseCookie.from("jwt", token)
        .httpOnly(true)
        .secure(false)         // true i production om du kör HTTPS
        .path("/")
        .maxAge(3600)
        .sameSite("None")      // Viktigt om frontend ≠ backend domän
        .build();

return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(responseBody);


⚠️ Om du använder SameSite=None → Secure måste vara true i production, annars blockeras cookien i Chrome.
     */

}
