package ivar.hogblom.crmbackend.datasource;

// package ivar.hogblom.crmbackend.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ivar.hogblom.crmbackend.system.entity.db.DatabaseConnection;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class PostgresDataSourceFactory implements DynamicDataSourceFactory {

    @Override
    public boolean supports(String type) {
        return "postgres".equalsIgnoreCase(type);
    }

    @Override
    public DataSource create(DatabaseConnection c) {
        HikariConfig h = new HikariConfig();
        h.setDriverClassName("org.postgresql.Driver");
        String url = "jdbc:postgresql://" + c.getHost() + ":" + c.getPort() + "/" + c.getDatabaseName();
        h.setJdbcUrl(url);
        h.setUsername(c.getUsername());
        h.setPassword(c.getPassword());
        h.setMaximumPoolSize(10);
        return new HikariDataSource(h);
    }
}

