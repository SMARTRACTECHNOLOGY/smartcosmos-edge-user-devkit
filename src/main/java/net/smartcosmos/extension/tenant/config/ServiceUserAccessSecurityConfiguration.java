package net.smartcosmos.extension.tenant.config;

import net.smartcosmos.extension.tenant.auth.provider.ServiceUserAccessAuthenticationProvider;
import net.smartcosmos.extension.tenant.auth.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(ServiceUserProperties.class)
@ComponentScan("net.smartcosmos.extension.tenant.auth")
public class ServiceUserAccessSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private ServiceUserAccessAuthenticationProvider serviceUserAuthProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(serviceUserAuthProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
            .antMatchers("/authenticate/**")
            .and()
            .authorizeRequests().anyRequest().authenticated();

        http.addFilterBefore(new AuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class);
    }
}
