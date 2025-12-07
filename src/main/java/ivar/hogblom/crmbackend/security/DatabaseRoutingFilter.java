package ivar.hogblom.crmbackend.security;

// package ivar.hogblom.crmbackend.security;

import ivar.hogblom.crmbackend.datasource.DynamicRoutingDataSource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class DatabaseRoutingFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    public DatabaseRoutingFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = jwtTokenUtil.resolveToken(request);
            if (token != null && jwtTokenUtil.validateToken(token)) {
                String dbKey = jwtTokenUtil.getDbKey(token);
                if (dbKey != null) {
                    DynamicRoutingDataSource.setCurrentKey(dbKey);
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            DynamicRoutingDataSource.clear(); // Gör så att inte User B kör queries mot User A:s databas.
        }
    }
}
