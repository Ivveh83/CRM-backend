package ivar.hogblom.crmbackend.system.service.db;

import ivar.hogblom.crmbackend.dto.db.DatasourceResponseDto;
import ivar.hogblom.crmbackend.security.JwtTokenUtil;
import ivar.hogblom.crmbackend.security.TokenBlacklistStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("databaseConnectionSecured")
@Transactional(transactionManager = "systemTransactionManager")
class DatabaseConnectionSecurityDecorator implements DatabaseConnectionService {

    private final DatabaseConnectionService delegate;
    private final TokenBlacklistStorage tokenBlacklistStorage;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public DatabaseConnectionSecurityDecorator(
            @Qualifier("databaseConnectionCore")DatabaseConnectionService delegate,
            TokenBlacklistStorage tokenBlacklistStorage,
            JwtTokenUtil jwtTokenUtil) {
        this.delegate = delegate;
        this.tokenBlacklistStorage = tokenBlacklistStorage;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public DatasourceResponseDto connectToDatabase(
            UUID id,
            UserDetails principal,
            String authHeader
    ) {
        // 1️⃣ huvuddomän först,
        DatasourceResponseDto result =
                delegate.connectToDatabase(id, principal, authHeader);

        // 2️⃣ svartlista det gamla token
        String oldToken = authHeader.replace("Bearer ", "");

        tokenBlacklistStorage.blacklistToken(
                oldToken,
                principal.getUsername(),
                jwtTokenUtil.getExpirationDateFromToken(oldToken).toInstant()
        );

        return result;
    }
}

