package ivar.hogblom.crmbackend.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ivar.hogblom.crmbackend.system.entity.db.DatabaseConnection;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class MySqlDataSourceFactory implements DynamicDataSourceFactory {

    @Override
    public boolean supports(String type) {
        return "mysql".equalsIgnoreCase(type) || "mariadb".equalsIgnoreCase(type);
    }

    @Override
    public DataSource create(DatabaseConnection c) {
        HikariConfig h = new HikariConfig();
        h.setDriverClassName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://" + c.getHost() + ":" + c.getPort() + "/" + c.getDatabaseName()
                + "?useSSL=false&serverTimezone=UTC";
        h.setJdbcUrl(url);
        h.setUsername(c.getUsername());
        h.setPassword(c.getPassword());
        h.setMaximumPoolSize(10);
        return new HikariDataSource(h);
    }
}
