package ivar.hogblom.crmbackend.system.service.db;

import ivar.hogblom.crmbackend.dto.db.DataSourceConfigDto;
import ivar.hogblom.crmbackend.dto.db.DatabaseConnectionResponseDto;
import ivar.hogblom.crmbackend.dto.db.DatasourceResponseDto;
import ivar.hogblom.crmbackend.dto.db.DisconnectResponseDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

public interface DatabaseConnectionService {
    DatabaseConnectionResponseDto create(DataSourceConfigDto dto, UserDetails principal);
    List<DatabaseConnectionResponseDto> findAllForUser(UserDetails principal);
    DatasourceResponseDto connectToDatabase(UUID id, UserDetails principal, String authHeader);
    DisconnectResponseDto disconnectFromDatabase(UserDetails principal, String authHeader);
    void delete(UUID id, UserDetails principal, String authHeader);
}
