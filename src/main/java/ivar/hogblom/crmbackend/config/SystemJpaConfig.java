package ivar.hogblom.crmbackend.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "ivar.hogblom.crmbackend.system.repository",
        entityManagerFactoryRef = "systemEntityManagerFactory",
        transactionManagerRef = "systemTransactionManager"
)
public class SystemJpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean systemEntityManagerFactory(
            @Qualifier("systemDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder
    ) {
        Map<String, Object> jpaProps = new HashMap<>();

        // ✅ SystemDB FÅR skapa/uppdatera schema
        jpaProps.put("hibernate.hbm2ddl.auto", "update");
        jpaProps.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        jpaProps.put("hibernate.show_sql", true);

        return builder
                .dataSource(dataSource)
                .packages("ivar.hogblom.crmbackend.system.entity")
                .persistenceUnit("system")
                .properties(jpaProps)
                .build();
    }

    @Bean
    public PlatformTransactionManager systemTransactionManager(
            @Qualifier("systemEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
