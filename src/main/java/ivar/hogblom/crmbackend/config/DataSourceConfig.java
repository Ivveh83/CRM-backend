package ivar.hogblom.crmbackend.config;

import ivar.hogblom.crmbackend.datasource.DynamicRoutingDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    /**
     * ✅ SYSTEM DB (H2) – används för users, roles, connections
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.system")
    public DataSource systemDataSource() {
        // här använder du redan H2 via application.properties
        return org.springframework.boot.jdbc.DataSourceBuilder.create().build();
    }

    /**
     * ✅ ROUTING datasource för CRM
     */
    @Bean
    public DynamicRoutingDataSource routingDataSource(DataSource systemDataSource) {

        Map<Object, Object> targets = new HashMap<>();

        // 🔑 bootstrap target (KRAV!)
        targets.put("bootstrap", systemDataSource);

        DynamicRoutingDataSource ds = new DynamicRoutingDataSource();
        ds.setTargetDataSources(targets);
        ds.setDefaultTargetDataSource(systemDataSource);
        ds.afterPropertiesSet();

        return ds;
    }

    /**
     * ✅ CRM DataSource = routing
     */
    @Bean
    public DataSource crmDataSource(DynamicRoutingDataSource routingDataSource) {
        return routingDataSource;
    }
}
