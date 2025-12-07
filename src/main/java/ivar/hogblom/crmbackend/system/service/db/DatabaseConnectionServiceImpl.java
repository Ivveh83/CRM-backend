package ivar.hogblom.crmbackend.system.service.db;

import ivar.hogblom.crmbackend.datasource.DynamicDataSourceManager;
import ivar.hogblom.crmbackend.dto.db.DatasourceResponseDto;
import ivar.hogblom.crmbackend.system.entity.db.DatabaseConnection;
import ivar.hogblom.crmbackend.system.entity.userEntityAndRole.UserEntity;
import ivar.hogblom.crmbackend.system.repository.db.DatabaseConnectionRepository;
import ivar.hogblom.crmbackend.system.repository.userEntityAndRole.UserEntityRepository;
import ivar.hogblom.crmbackend.security.JwtTokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("databaseConnectionCore")
@AllArgsConstructor
public class DatabaseConnectionServiceImpl implements DatabaseConnectionService {

    private final DatabaseConnectionRepository connectionRepository;
    private final DynamicDataSourceManager dataSourceManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserEntityRepository userEntityRepository;

    @Override
    @Transactional(transactionManager = "systemTransactionManager")
    public DatasourceResponseDto connectToDatabase(UUID id, UserDetails principal, String authHeader) {

        // Hämta user & connection, mappa UserDetails -> UserEntity
        String username = principal.getUsername();
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow();

        DatabaseConnection connection = connectionRepository.findByIdAndOwner(id, userEntity)
                .orElseThrow(() -> new RuntimeException("Connection not found or not allowed"));

        // Skapa/aktivera datasource
        String dbKey = dataSourceManager.activateConnection(connection);

        // skapa nytt token med dbKey
        String newToken = jwtTokenUtil.generateToken(principal, dbKey);

        // Returnera nytt token och dbKey
        return new DatasourceResponseDto(newToken, dbKey);
    }
}
