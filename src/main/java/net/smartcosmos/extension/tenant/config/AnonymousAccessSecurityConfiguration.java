package net.smartcosmos.extension.tenant.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class AnonymousAccessSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
            .ignoringAntMatchers("/tenants").disable()
            .antMatcher("/tenants/**").authorizeRequests()
            .antMatchers(HttpMethod.POST, "/tenants").permitAll()
            .anyRequest().authenticated();
    }
}
