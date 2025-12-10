package ivar.hogblom.crmbackend.datasource;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class SchemaInitializer {

    public void ensureSchema(DataSource dataSource) {
        String sql = loadSchemaSql();
        executeSql(dataSource, sql);
    }

    private String loadSchemaSql() {
        String path = "schema/sqlite.sql";

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalStateException("Schema file not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SQLite schema", e);
        }
    }

    private void executeSql(DataSource ds, String sql) {
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String raw : sql.split(";")) {
                String s = raw.trim();
                if (!s.isEmpty()) {
                    stmt.execute(s);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Schema initialization failed", e);
        }
    }
}
