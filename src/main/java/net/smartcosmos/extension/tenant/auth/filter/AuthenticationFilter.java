package net.smartcosmos.extension.tenant.auth.filter;

import lombok.extern.slf4j.Slf4j;
import net.smartcosmos.extension.tenant.auth.SmartCosmosServiceUser;
import net.smartcosmos.security.user.SmartCosmosUser;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

@Slf4j
public class AuthenticationFilter extends GenericFilterBean {

    private AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Optional<String> authorizationHeader = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));

        Optional<Authentication> authenticationToken = getAuthentication(authorizationHeader);
        if (authenticationToken.isPresent()) {
            Authentication authentication = authenticate(authenticationToken.get());
            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        log.debug("SMART COSMOS AuthenticationFilter is passing request down the filter chain");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private Optional<Authentication> getAuthentication(Optional<String> authorizationHeader) {

        Authentication authenticationToken = null;
        if (authorizationHeader.isPresent()) {
            authenticationToken = getUsernamePasswordToken(authorizationHeader.get());
        }

        return Optional.ofNullable(authenticationToken);
    }

    private Authentication getUsernamePasswordToken(String authorization) {

        if (authorization.startsWith("Basic ")) {

            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(Base64.decodeBase64(base64Credentials), Charset.forName("UTF-8"));
            final String[] values = credentials.split(":",2);

            SmartCosmosUser serviceUser = SmartCosmosServiceUser.getServiceUser(values[0], values[1], null);

            return new UsernamePasswordAuthenticationToken(serviceUser, values[1]);
        }

        return null;
    }

    private Authentication authenticate(Authentication requestAuthentication) {

        Authentication responseAuthentication = authenticationManager.authenticate(requestAuthentication);
        if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
            throw new InternalAuthenticationServiceException("Unable to authenticate user for provided credentials");
        }
        log.debug("User successfully authenticated");

        return responseAuthentication;
    }
}
