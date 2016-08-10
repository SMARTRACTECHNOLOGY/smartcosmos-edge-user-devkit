package net.smartcosmos.extension.tenant.rest.dto.user;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
//@ApiModel(description = "Create a \"Tenant\" in the smartcosmos-edge-tenant Server.")
public class RestCreateOrUpdateUserRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    String username;
    String emailAddress;
    String givenName;
    String surname;
    String password;
    Set<String> roles;
    Boolean active;

    @Builder
    @ConstructorProperties({ "username", "emailAddress", "givenName", "surname", "password", "roles", "active" })
    public RestCreateOrUpdateUserRequest(
        String username, String emailAddress, String givenName, String surname, String password, Collection<String> roles,
        Boolean active) {

        this.username = username;
        this.emailAddress = emailAddress;
        this.givenName = givenName;
        this.surname = surname;
        this.password = password;
        this.roles = new HashSet<>();
        if (roles != null && !roles.isEmpty()) {
            this.roles.addAll(roles);
        }
        this.active = active;
    }
}
