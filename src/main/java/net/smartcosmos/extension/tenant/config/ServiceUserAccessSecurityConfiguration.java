package net.smartcosmos.extension.tenant.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import net.smartcosmos.extension.tenant.auth.ServiceUserAccessAuthenticationProvider;

@Configuration
@EnableWebSecurity
@ComponentScan("net.smartcosmos.extension.tenant.auth")
public class ServiceUserAccessSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private ServiceUserAccessAuthenticationProvider authProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//        http.authenticationProvider(authProvider)
//            .csrf().disable()
//            .antMatcher("/authenticate/**").authorizeRequests()
//            .anyRequest().authenticated();
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .antMatcher("/authenticate/**")
            .authorizeRequests().anyRequest().authenticated()
            .and()
            .httpBasic();
    }
}
