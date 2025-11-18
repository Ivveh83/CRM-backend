package ivar.hogblom.crmbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtRequestFilter intercepts every HTTP request before it hits your controller, checks for a JWT token, and authenticates the user if valid.
 * It ensures that the user is authenticated and their details are set in the SecurityContext.
 * This filter is stateless and does not maintain any session information.
 * It is typically used in conjunction with a JwtTokenUtil class that handles the creation and validation of JWT tokens.
 * This filter is registered in the SecurityConfig class to be applied to all requests.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenBlacklistStorage tokenBlacklistStorage;

    public JwtRequestFilter(UserDetailsService userDetailsService,
                            JwtTokenUtil jwtTokenUtil,
                            TokenBlacklistStorage tokenBlacklistStorage) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenBlacklistStorage = tokenBlacklistStorage;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        try {

            // --- 1. Svartlistad token ---
            if (tokenBlacklistStorage.isBlacklisted(jwt)) {
                throw new BadCredentialsException("Token has been revoked");
            }

            // --- 2. Plocka ut användarnamn ---
            String username = jwtTokenUtil.getUsernameFromToken(jwt);
            if (username == null) {
                throw new BadCredentialsException("Invalid JWT: username missing");
            }

            // --- 3. Undvik dubbel-autentisering ---
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // --- 4. Validera token ---
                if (!jwtTokenUtil.validateToken(jwt, userDetails)) {
                    throw new BadCredentialsException("Invalid or expired token");
                }

                // --- 5. Skapa authentication ---
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // --- 6. Lägg in i SecurityContext ---
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (BadCredentialsException ex) {
            // LÅT SECURITYKEDJAN TA DET
            SecurityContextHolder.clearContext();
            throw ex; // <-- Viktigt!
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            throw new BadCredentialsException("Invalid token", ex);
        }

        chain.doFilter(request, response);
    }
}