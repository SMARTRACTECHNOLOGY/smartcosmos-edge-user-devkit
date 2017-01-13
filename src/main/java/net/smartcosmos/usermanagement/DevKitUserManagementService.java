package net.smartcosmos.usermanagement;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.smartcosmos.annotation.EnableSmartCosmosEvents;
import net.smartcosmos.annotation.EnableSmartCosmosExtension;
import net.smartcosmos.annotation.EnableSmartCosmosMonitoring;
import net.smartcosmos.usermanagement.config.AnonymousAccessSecurityConfiguration;
import net.smartcosmos.usermanagement.config.UserManagementPersistenceConfig;

@EnableSmartCosmosExtension
@EnableSmartCosmosEvents
@EnableSmartCosmosMonitoring
//@EnableSmartCosmosSecurity
@ComponentScan
@Import({ AnonymousAccessSecurityConfiguration.class, UserManagementPersistenceConfig.class })
public class DevKitUserManagementService {

    public static void main(String[] args) {

        new SpringApplicationBuilder(DevKitUserManagementService.class).web(true)
            .run(args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
