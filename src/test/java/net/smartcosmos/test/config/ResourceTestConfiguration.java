package net.smartcosmos.test.config;

import org.mockito.*;
import org.springframework.context.annotation.Bean;

import net.smartcosmos.usermanagement.role.persistence.RoleDao;
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;

/**
 * <p>Configuration class to be particularly used for resource tests.</p>
 * <p>Note that this class doesn't use the {@code @Configuration} annotation to avoid side-effects on other tests caused by
 * use of the {@code @ComponentScan} annotation.</p>
 */
public class ResourceTestConfiguration {

    @Bean
    public TenantDao tenantDao() {

        return (Mockito.mock(TenantDao.class));
    }

    @Bean
    public RoleDao roleDao() {

        return (Mockito.mock(RoleDao.class));
    }
}
