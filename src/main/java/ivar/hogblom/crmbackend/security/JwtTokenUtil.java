package ivar.hogblom.crmbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JwtTokenUtil is a utility class for generating and validating JWT tokens.
 * It uses the io.jsonwebtoken library to create and parse JWT tokens.
 * It provides methods to generate a token from UserDetails, extract the username from a token,
 * validate a token against UserDetails, and check if a token is expired.
 *
 */
@Component
public class JwtTokenUtil {
    private final TokenBlacklistStorage tokenBlacklistStorage;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public JwtTokenUtil(TokenBlacklistStorage tokenBlacklistStorage) {
        this.tokenBlacklistStorage = tokenBlacklistStorage;
    }

    public String generateToken(UserDetails userDetails, String dbkey) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("tokenVersion", tokenBlacklistStorage.getUserTokenVersion(userDetails.getUsername()));

        if (dbkey != null) {
            claims.put("dbKey", dbkey);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getDbKey(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("dbKey", String.class);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = getClaimsFromToken(token);
            String username = claims.getSubject();
            Integer tokenVersion = claims.get("tokenVersion", Integer.class);
            int currentVersion = tokenBlacklistStorage.getUserTokenVersion(username);

            return username.equals(userDetails.getUsername()) &&
                    !isTokenExpired(token) &&
                    tokenVersion != null &&
                    tokenVersion == currentVersion;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);

            String username = claims.getSubject();
            Integer tokenVersion = claims.get("tokenVersion", Integer.class);

            if (username == null || tokenVersion == null) {
                return false;
            }

            int currentVersion = tokenBlacklistStorage.getUserTokenVersion(username);

            return tokenVersion == currentVersion && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }


    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }
}