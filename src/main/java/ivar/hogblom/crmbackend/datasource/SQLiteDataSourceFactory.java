package ivar.hogblom.crmbackend.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ivar.hogblom.crmbackend.system.entity.db.DatabaseConnection;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class SQLiteDataSourceFactory implements DynamicDataSourceFactory {

    @Override
    public boolean supports(String type) {
        return "sqlite".equalsIgnoreCase(type)
                || "sqlcipher".equalsIgnoreCase(type);
    }

    @Override
    public DataSource create(DatabaseConnection c) {
        HikariConfig h = new HikariConfig();
        h.setDriverClassName("org.sqlite.JDBC");
        h.setJdbcUrl("jdbc:sqlite:" + c.getFilePath());
        h.setMaximumPoolSize(5);

        boolean encrypted =
                c.getEncryptionKey() != null && !c.getEncryptionKey().isBlank();

        if (encrypted) {
            h.addDataSourceProperty("pragma", "key='" + c.getEncryptionKey() + "'");
        }

        HikariDataSource ds = new HikariDataSource(h);

        // ✅ VERIFIERA DIREKT
        try (Connection conn = ds.getConnection();
             Statement st = conn.createStatement()) {

            // Harmlös SQLCipher-säker kontroll
            st.execute("SELECT count(*) FROM sqlite_master");

        } catch (Exception e) {
            ds.close();

            throw new IllegalStateException(
                    encrypted
                            ? "Invalid encryption key for SQLite database"
                            : "Failed to open SQLite database",
                    e
            );
        }

        return ds;
    }
}
