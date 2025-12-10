package ivar.hogblom.crmbackend.datasource;

import ivar.hogblom.crmbackend.system.entity.db.DatabaseConnection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class DynamicDataSourceManager {

    private final SQLiteDataSourceFactory sqliteDataSourceFactory;
    private final DynamicRoutingDataSource routingDataSource;
    private final SchemaInitializer schemaInitializer;

    // Cache per connectionId
    private final Map<UUID, DataSource> cache = new ConcurrentHashMap<>();

    public String activateConnection(DatabaseConnection conn) {

        // 🔐 Säkerhet: vi stödjer endast SQLite
        if (!"sqlite".equalsIgnoreCase(conn.getType())) {
            throw new IllegalArgumentException(
                    "Only SQLite databases are supported"
            );
        }

        // 1️⃣ Bygg nyckeln EN gång
        final String key = "conn_" + conn.getId();

        // 2️⃣ Hämta eller skapa DataSource + initiera schema + registrera i routing
        cache.computeIfAbsent(conn.getId(), id -> {

            // Skapa SQLite DataSource
            DataSource created = sqliteDataSourceFactory.create(conn);

            // Initiera schema (idempotent – körs bara första gången)
            schemaInitializer.ensureSchema(created);

            // Registrera i routing
            routingDataSource.addDataSource(key, created);

            return created;
        });

        // 3️⃣ Sätt aktuell datasource för tråden
        DynamicRoutingDataSource.setCurrentKey(key);

        // 4️⃣ Returnera dbKey (samma som används i token / frontend)
        return key;
    }

    public void clearCurrent() {
        DynamicRoutingDataSource.clear();
    }
}
