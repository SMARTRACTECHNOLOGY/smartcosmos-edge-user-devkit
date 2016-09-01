package net.smartcosmos.extension.tenant;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.smartcosmos.annotation.EnableSmartCosmosEvents;
import net.smartcosmos.annotation.EnableSmartCosmosExtension;
import net.smartcosmos.extension.tenant.config.AnonymousAccessSecurityConfiguration;

@EnableSmartCosmosExtension
@EnableSmartCosmosEvents
@EnableSmartCosmosMonitoring
//@EnableSmartCosmosSecurity
@ComponentScan
@Import(AnonymousAccessSecurityConfiguration.class)
public class DevKitUserManagementService {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DevKitUserManagementService.class).web(true)
            .run(args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
