package net.smartcosmos.extension.tenant;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import net.smartcosmos.cluster.userdetails.UserDetailsPersistenceConfig;

@EnableJpaRepositories
@EnableJpaAuditing
@EntityScan
@ComponentScan
@Configuration
@Import({ UserDetailsPersistenceConfig.class })
public class UserManagementPersistenceConfig {

}
