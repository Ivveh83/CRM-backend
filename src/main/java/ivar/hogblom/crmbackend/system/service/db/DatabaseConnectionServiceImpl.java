package ivar.hogblom.crmbackend.system.service.db;

import ivar.hogblom.crmbackend.datasource.DynamicDataSourceManager;
import ivar.hogblom.crmbackend.dto.db.DataSourceConfigDto;
import ivar.hogblom.crmbackend.dto.db.DatabaseConnectionResponseDto;
import ivar.hogblom.crmbackend.dto.db.DatasourceResponseDto;
import ivar.hogblom.crmbackend.dto.db.DisconnectResponseDto;
import ivar.hogblom.crmbackend.system.entity.db.DatabaseConnection;
import ivar.hogblom.crmbackend.system.entity.userEntityAndRole.UserEntity;
import ivar.hogblom.crmbackend.system.repository.db.DatabaseConnectionRepository;
import ivar.hogblom.crmbackend.system.repository.userEntityAndRole.UserEntityRepository;
import ivar.hogblom.crmbackend.security.JwtTokenUtil;
import ivar.hogblom.crmbackend.system.service.db.security.CryptoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service("databaseConnectionCore")
@AllArgsConstructor
public class DatabaseConnectionServiceImpl implements DatabaseConnectionService {

    private final DatabaseConnectionRepository connectionRepository;
    private final DynamicDataSourceManager dataSourceManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserEntityRepository userEntityRepository;


    @Override
    public DatabaseConnectionResponseDto create(
            DataSourceConfigDto dto,
            UserDetails principal
    ) {
        UserEntity owner = userEntityRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Problem with finding logged in user"));

        validate(dto);


        DatabaseConnection conn = DatabaseConnection.builder()
                .type(dto.getType())
                .host(dto.getHost())
                .port(dto.getPort())
                .databaseName(dto.getDatabase())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .filePath(dto.getFilePath())
                .owner(owner)
                .build();

        DatabaseConnection saved = connectionRepository.save(conn);
        return map(saved);
    }

    @Override
    public List<DatabaseConnectionResponseDto> findAllForUser(UserDetails principal) {
        return connectionRepository.findByOwnerUsername(principal.getUsername())
                .stream()
                .map(this::map)
                .toList();
    }

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

        //Returnera endast dbKey, dekoratorn skapar nytt token
        return new DatasourceResponseDto(null, dbKey);
    }

    @Override
    public DisconnectResponseDto disconnectFromDatabase(UserDetails principal, String authHeader) {
        return DisconnectResponseDto.builder().build();
    }

    private DatabaseConnectionResponseDto map(DatabaseConnection c) {
        return DatabaseConnectionResponseDto.builder()
                .id(c.getId())
                .type(c.getType())
                .databaseName(c.getDatabaseName())
                .filePath(c.getFilePath())
                .build();
    }

    public void delete(UUID id, UserDetails principal, String authHeader) {

        DatabaseConnection connection = connectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Database connection not found"
                ));

        if (!connection.getOwner().getUsername().equals(principal.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Not allowed to delete this connection"
            );
        }

        // ✅ Kontrollera om denna DB är aktiv i token
        String token = authHeader.replace("Bearer ", "");
        String activeDbKey = jwtTokenUtil.getDbKey(token); // ← viktig

        String thisDbKey = "conn_" + connection.getId();

        if (thisDbKey.equals(activeDbKey)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Disconnect the database before deleting it"
            );
        }

        connectionRepository.delete(connection);
    }



    private void validate(DataSourceConfigDto dto) {
        switch (dto.getType().toLowerCase()) {

            case "sqlite" -> {
                if (dto.getFilePath() == null || dto.getFilePath().isBlank()) {
                    throw new IllegalArgumentException("SQLite requires filePath");
                }
            }

            /*
            If the DynamicDatasourceFactory gets more implementations of server databases, use also this:
            case "postgres", "mysql", "mariadb" -> {
                if (dto.getHost() == null || dto.getHost().isBlank()) {
                    throw new IllegalArgumentException("Host is required");
                }
                if (dto.getPort() == null) {
                    throw new IllegalArgumentException("Port is required");
                }
                if (dto.getDatabase() == null || dto.getDatabase().isBlank()) {
                    throw new IllegalArgumentException("Database name is required");
                }
                if (dto.getUsername() == null || dto.getUsername().isBlank()) {
                    throw new IllegalArgumentException("Username is required");
                }
            }*/

            default -> throw new IllegalArgumentException("Unsupported database type: " + dto.getType());
        }
    }

}

// Om SQLCipher används, lägg till CryptoService i controller.
//@Override
//public DatabaseConnectionResponseDto create(
//        DataSourceConfigDto dto,
//        UserDetails principal
//) {
//    UserEntity owner = userEntityRepository.findByUsername(principal.getUsername())
//            .orElseThrow(() -> new IllegalArgumentException("Problem with finding logged in user"));
//
//    validate(dto);
//
//    String encryptedKey =
//            StringUtils.hasText(dto.getEncryptionKey())
//                    ? cryptoService.encrypt(dto.getEncryptionKey())
//                    : null;
//
//
//    DatabaseConnection conn = DatabaseConnection.builder()
//            .type(dto.getType())
//            .host(dto.getHost())
//            .port(dto.getPort())
//            .databaseName(dto.getDatabase())
//            .username(dto.getUsername())
//            .password(dto.getPassword())
//            .filePath(dto.getFilePath())
//            .encryptionKey(encryptedKey)
//            .owner(owner)
//            .build();
//
//    DatabaseConnection saved = connectionRepository.save(conn);
//    return map(saved);
//}