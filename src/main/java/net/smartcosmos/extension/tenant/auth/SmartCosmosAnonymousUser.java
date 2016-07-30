package net.smartcosmos.extension.tenant.auth;

import net.smartcosmos.security.user.SmartCosmosUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public interface SmartCosmosAnonymousUser {

    String ANONYMOUS_AUTHENTICATION_KEY = "0e11c838-9b18-4796-98ad-f9dfa946aec1";

    String ANONYMOUS_USER_NAME = "ANONYMOUS";

    String ANONYMOUS_USER_ROLE = "ROLE_ANONYMOUS";

    Collection<GrantedAuthority> ANONYMOUS_USER_AUTHORITIES = anonymousUserAuthorities();

    SmartCosmosUser ANONYMOUS_USER = new SmartCosmosUser(null, null, ANONYMOUS_USER_NAME, "", ANONYMOUS_USER_AUTHORITIES);

    static Collection<GrantedAuthority> anonymousUserAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("hasRole('" + ANONYMOUS_USER_ROLE + "')"));

        return authorities;
    }
}
