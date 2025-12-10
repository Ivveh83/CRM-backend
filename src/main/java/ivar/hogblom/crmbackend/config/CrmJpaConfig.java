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
        basePackages = "ivar.hogblom.crmbackend.crm.repository",
        entityManagerFactoryRef = "crmEntityManagerFactory",
        transactionManagerRef = "crmTransactionManager"
)
public class CrmJpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean crmEntityManagerFactory(
            @Qualifier("crmDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder
    ) {
        Map<String, Object> jpaProps = new HashMap<>();

        // 🚨 EXTREMT VIKTIGT
        // CRM-databaser får ALDRIG auto-skapas / ändras
        jpaProps.put("hibernate.hbm2ddl.auto", "none");

        // Hibernate kan oftast lista ut dialect själv via JDBC metadata
        // Om du vill sätta explicit per DB kan detta göras senare
        // jpaProps.put("hibernate.dialect", "...");

        // ✅ EXPLICIT SQLite-dialect (VIKTIGT vid routing datasource)
        jpaProps.put(
                "hibernate.dialect",
                "org.hibernate.community.dialect.SQLiteDialect"
        );


        jpaProps.put("hibernate.show_sql", false);

        return builder
                .dataSource(dataSource)
                .packages(
                        "ivar.hogblom.crmbackend.crm.entity",
                                        "ivar.hogblom.crmbackend.config.jpa")
                .persistenceUnit("crm")
                .properties(jpaProps)
                .build();
    }

    @Bean
    public PlatformTransactionManager crmTransactionManager(
            @Qualifier("crmEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
