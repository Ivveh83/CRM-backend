package ivar.hogblom.crmbackend.crm.security;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireCrmDatabase {
}
