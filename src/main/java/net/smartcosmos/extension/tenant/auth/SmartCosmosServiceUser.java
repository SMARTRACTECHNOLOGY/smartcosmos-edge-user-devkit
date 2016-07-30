package net.smartcosmos.extension.tenant.auth;

import net.smartcosmos.security.user.SmartCosmosUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public interface SmartCosmosServiceUser {

    String SERVICE_USER_ROLE = "ROLE_SMARTCOSMOS_SERVICE_CLIENT";

    Collection<GrantedAuthority> DEFAULT_SERVICE_USER_AUTHORITIES = defaultServiceUserAuthorities();

    static Collection<GrantedAuthority> defaultServiceUserAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("hasRole('" + SERVICE_USER_ROLE + "')"));

        return authorities;
    }

    static SmartCosmosUser getServiceUser(String username, String password, Collection<GrantedAuthority> authorities) {

        Collection<GrantedAuthority> serviceUserAuthorities = new ArrayList<>();
        if (authorities == null || authorities.isEmpty()) {
            serviceUserAuthorities = DEFAULT_SERVICE_USER_AUTHORITIES;
        } else {
            serviceUserAuthorities.addAll(authorities);
        }

        return new SmartCosmosUser(null, null, username, password, serviceUserAuthorities);
    }
}
