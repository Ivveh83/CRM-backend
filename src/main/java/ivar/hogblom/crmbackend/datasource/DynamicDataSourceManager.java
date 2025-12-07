package ivar.hogblom.crmbackend.datasource;

import ivar.hogblom.crmbackend.system.entity.db.DatabaseConnection;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DynamicDataSourceManager {

    private final List<DynamicDataSourceFactory> factories;
    private final DynamicRoutingDataSource routingDataSource;

    // cache per connectionId
    private final Map<UUID, DataSource> cache = new ConcurrentHashMap<>();

    public DynamicDataSourceManager(List<DynamicDataSourceFactory> factories,
                                    DynamicRoutingDataSource routingDataSource) {
        this.factories = factories;
        this.routingDataSource = routingDataSource;
    }

    public String activateConnection(DatabaseConnection conn) {
        DataSource ds = cache.computeIfAbsent(conn.getId(), id -> {
            DynamicDataSourceFactory factory = factories.stream()
                    .filter(f -> f.supports(conn.getType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unsupported DB type: " + conn.getType()));

            return factory.create(conn);
        });

        String key = "conn_" + conn.getId();
        routingDataSource.addDataSource(key, ds);
        //DynamicRoutingDataSource.setCurrentKey(key); dbKey sätts istället per request (via filter), inte globalt här, annars risk för fel DB i samma tråd.
        return key;
    }

    public void clearCurrent() {
        DynamicRoutingDataSource.clear();
    }
}

