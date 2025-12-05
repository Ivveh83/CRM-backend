package ivar.hogblom.crmbackend.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("ivar.hogblom.crmbackend.*")
public class AppConfig {


    /*
    @Bean
    @Primary
    public ContractService ContractValidationServiceImpl(ContractService contractService) {
        return new ContractValidatingServiceImpl(contractService);
    }*/

}