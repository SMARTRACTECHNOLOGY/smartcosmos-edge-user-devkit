package net.smartcosmos.extension.tenant.auth.provider;

import net.smartcosmos.extension.tenant.config.ServiceUserProperties;
import net.smartcosmos.security.user.SmartCosmosUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ServiceUserAccessAuthenticationProvider implements AuthenticationProvider {

    private static final Class SUPPORTED_AUTHENTICATION = UsernamePasswordAuthenticationToken.class;

    @Autowired
    private ServiceUserProperties serviceUser;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (authentication == null) {
            throw new InsufficientAuthenticationException("authentication must not be null");
        }

        String username = authentication.getName();
        Object credentials = authentication.getCredentials();
        Object principal = authentication.getPrincipal();

        if (credentials instanceof String && principal instanceof SmartCosmosUser) {
            String password = (String) credentials;
            if (StringUtils.equals(username, serviceUser.getUsername())
                && StringUtils.equals(password, serviceUser.getPassword())) {

                SmartCosmosUser user = (SmartCosmosUser) principal;
                Collection<GrantedAuthority> authorities = user.getAuthorities();

                return new UsernamePasswordAuthenticationToken(user, credentials, authorities);
            } else {
                String msg = String.format("Credentials for user '%s' do not match.", authentication.getName());
                throw new BadCredentialsException(msg);
            }
        }

        // We expect credentials to be a password String, and principal needs to be SmartCosmosUser.
        // If they're not, we don't know what to do.
        return null;
    }

    @Override
    public boolean supports(Class<?> authenticationClass) {
        return SUPPORTED_AUTHENTICATION.equals(authenticationClass);
    }
}
