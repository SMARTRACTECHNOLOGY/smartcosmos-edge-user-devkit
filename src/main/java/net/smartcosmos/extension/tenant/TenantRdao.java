package net.smartcosmos.extension.tenant;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import net.smartcosmos.extension.tenant.config.AnonymousAccessSecurityConfiguration;
import net.smartcosmos.spring.EnableSmartCosmos;
import net.smartcosmos.spring.EnableSmartCosmosExtension;
import net.smartcosmos.spring.EnableSmartCosmosSecurity;

@EnableSmartCosmosExtension
@EnableSmartCosmos
@EnableSmartCosmosSecurity
@EnableWebMvc
//@Import({ AnonymousAccessSecurityConfiguration.class, ServiceUserAccessSecurityConfiguration.class})
@Import({ AnonymousAccessSecurityConfiguration.class})
@ComponentScan
public class TenantRdao extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(TenantRdao.class).web(true).run(args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
