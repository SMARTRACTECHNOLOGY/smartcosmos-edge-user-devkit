package net.smartcosmos.extension.tenant.auth;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.config.ServiceUserProperties;

@Component
public class ServiceUserAccessAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ServiceUserProperties serviceUser;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (authentication == null) {
            throw new InsufficientAuthenticationException("authentication must not be null");
        }

        String username = authentication.getName();
        Object credentials = authentication.getCredentials();

        if (credentials instanceof String) {
            String password = (String) credentials;
            if (StringUtils.equals(username, serviceUser.getUsername())
                && StringUtils.equals(password, serviceUser.getPassword())) {
                return authentication;
            } else {
                String msg = String.format("Credentials for user '%s' do not match.", authentication.getName());
                throw new BadCredentialsException(msg);
            }
        }

        // We excpect credentials to be a password String.
        // If they're not, we don't know what to do.
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
