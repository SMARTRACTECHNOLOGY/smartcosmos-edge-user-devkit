package net.smartcosmos.extension.tenant.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import net.smartcosmos.extension.tenant.auth.SmartCosmosAnonymousUser;
import net.smartcosmos.security.user.SmartCosmosUserConfiguration;

@Configuration
// Put this in order before the typical SMART COSMOS Security
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER - 2)
@Slf4j
@EnableResourceServer
@Import(SmartCosmosUserConfiguration.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AnonymousAccessSecurityConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private Environment environment;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {

        // This is necessary since it'll clear the context removing the mock user otherwise.
        // see https://stackoverflow.com/questions/37573361/springsecurity-withsecuritycontext-mockmvc-oauth2-always-unauthorised
        resources.stateless(!environment.acceptsProfiles("test"));
    }

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
            .principal(SmartCosmosAnonymousUser.ANONYMOUS_USER);
    }
}
