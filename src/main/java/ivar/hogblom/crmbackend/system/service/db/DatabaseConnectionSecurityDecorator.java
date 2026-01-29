package ivar.hogblom.crmbackend.system.service.db;

import ivar.hogblom.crmbackend.dto.db.DataSourceConfigDto;
import ivar.hogblom.crmbackend.dto.db.DatabaseConnectionResponseDto;
import ivar.hogblom.crmbackend.dto.db.DatasourceResponseDto;
import ivar.hogblom.crmbackend.dto.db.DisconnectResponseDto;
import ivar.hogblom.crmbackend.security.JwtTokenUtil;
import ivar.hogblom.crmbackend.security.TokenBlacklistStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public List<DatabaseConnectionResponseDto> findAllForUser(UserDetails principal) {
        return delegate.findAllForUser(principal);
    }

    @Override
    public DatabaseConnectionResponseDto create(DataSourceConfigDto dto, UserDetails principal) {
        return delegate.create(dto, principal);
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

        // skapa nytt token med dbKey från core-domänen
        String newToken = jwtTokenUtil.generateToken(principal, result.dbKey());

        // mappa om result
        result = result.toBuilder()
                .token(newToken)
                .build();


        return result;
    }

    @Override
    public DisconnectResponseDto disconnectFromDatabase(UserDetails principal, String authHeader) {

        // 1️⃣ plocka ut gamla token
        String oldToken = authHeader.replace("Bearer ", "");

        // 2️⃣ svartlista gamla token EFTER att nytt är klart
        tokenBlacklistStorage.blacklistToken(
                oldToken,
                principal.getUsername(),
                jwtTokenUtil.getExpirationDateFromToken(oldToken).toInstant()
        );

        // 3️⃣ skapa nytt token UTAN dbKey
        String newToken = jwtTokenUtil.generateToken(principal, null);


        return new DisconnectResponseDto(newToken);
    }

    @Override
    public void delete(UUID id, UserDetails principal, String authHeader) {
        delegate.delete(id, principal, authHeader);
    }


}

