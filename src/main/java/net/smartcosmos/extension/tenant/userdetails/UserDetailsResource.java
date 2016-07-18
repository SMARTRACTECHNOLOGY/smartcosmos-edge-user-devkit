package net.smartcosmos.extension.tenant.userdetails;

import java.io.IOException;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.GetOrDeleteUserResponse;
import net.smartcosmos.extension.tenant.dto.UserDto;

@RestController
@Slf4j
public class UserDetailsResource {

    @Autowired
    PasswordEncoder passwordEncoder;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    TenantDao tenantDao;

    @RequestMapping(value = "{username}", method = RequestMethod.POST)
    public UserDto authenticate(
        @PathVariable("username") String username,
        @RequestBody byte[] requestBody)
        throws UsernameNotFoundException, IOException {

        final ObjectNode authentication = objectMapper.readValue(requestBody,
                                                                 ObjectNode.class);

        log.info("Requested information on username {} with {}", username,
                 authentication);

        Optional<GetOrDeleteUserResponse> userResponseOptional = tenantDao.findUserByName(username);

        if (userResponseOptional.isPresent()) {
            GetOrDeleteUserResponse userResponse = userResponseOptional.get();
            if (authentication.has("credentials")) {
                String credentials = authentication.get("credentials").asText();
                if (passwordEncoder.encode(credentials).equals(userResponse.getPassword())) {
                    return UserDto.builder()
                        .tenantUrn(userResponse.getTenantUrn())
                        .userUrn(userResponse.getUrn())
                        .username(userResponse.getUsername())
                        .passwordHash(userResponse.getPassword())
                        .roles(userResponse.getRoles())
                        .build();

                }
            }
        }
        throw new BadCredentialsException("Invalid password");
    }

    /**
     * The GET method only returns the details on the user, removing the password hash
     * component. This is not cached in the authorization server, and is merely a fallback
     * method for the user details service.
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     * @throws IOException
     */
    @RequestMapping(value = "{username}")
    public UserDto authenticate(@PathVariable("username") String username)
        throws UsernameNotFoundException, IOException {

        log.info("Requested information for details only on username {}", username);

        Optional<GetOrDeleteUserResponse> userResponseOptional = tenantDao.findUserByName(username);

        if (userResponseOptional.isPresent()) {
            GetOrDeleteUserResponse userResponse = userResponseOptional.get();
            return UserDto.builder()
                .tenantUrn(userResponse.getTenantUrn())
                .userUrn(userResponse.getUrn())
                .username(userResponse.getUsername())
                .roles(userResponse.getRoles())
                .build();

        }
        throw new UsernameNotFoundException("Username " + username + " not found");

    }
}
