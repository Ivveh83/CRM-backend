package ivar.hogblom.crmbackend.crm.security;

import ivar.hogblom.crmbackend.datasource.DynamicRoutingDataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CrmDatabaseGuardAspect {

    @Before("@within(RequireCrmDatabase) || @annotation(RequireCrmDatabase)")
    public void checkDatabaseSelected(JoinPoint joinPoint) {
        if (DynamicRoutingDataSource.getCurrentKey() == null) {
            throw new AccessDeniedException("No CRM database selected");
        }
    }
}
