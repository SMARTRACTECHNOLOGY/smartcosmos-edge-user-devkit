package net.smartcosmos.extension.tenant.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

import net.smartcosmos.extension.tenant.auth.SmartCosmosAnonymousUser;
import net.smartcosmos.security.user.SmartCosmosUserConfiguration;

@Configuration
// Put this in order before the typical SMART COSMOS Security
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER - 2)
@Slf4j
@EnableResourceServer
@Import(SmartCosmosUserConfiguration.class)
public class AnonymousAccessSecurityConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {

        log.debug("Overriding default security behavior.");
        http.authorizeRequests()
            .antMatchers(HttpMethod.POST, "/tenants")
            .permitAll()
            .and()
            .antMatcher("/**")
            .authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .csrf()
            .disable()
            .anonymous()
            .key(SmartCosmosAnonymousUser.ANONYMOUS_AUTHENTICATION_KEY)
            .principal(SmartCosmosAnonymousUser.ANONYMOUS_USER))
    }
}
