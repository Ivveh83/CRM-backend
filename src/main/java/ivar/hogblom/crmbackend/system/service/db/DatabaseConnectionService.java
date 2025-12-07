package ivar.hogblom.crmbackend.system.service.db;

import ivar.hogblom.crmbackend.dto.db.DatasourceResponseDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface DatabaseConnectionService {
    DatasourceResponseDto connectToDatabase(UUID id, UserDetails principal, String authHeader);
}
