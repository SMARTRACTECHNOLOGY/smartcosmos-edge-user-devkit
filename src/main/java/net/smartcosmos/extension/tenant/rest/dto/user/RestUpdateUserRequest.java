package net.smartcosmos.extension.tenant.rest.dto.user;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

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
public class RestUpdateUserRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    String username;
    String emailAddress;
    String givenName;
    String surname;
    List<String> roles;
    Boolean active;

    @Builder
    @ConstructorProperties({ "username", "emailAddress", "givenName", "surname", "roles", "active" })
    public RestUpdateUserRequest(
        String username, String emailAddress, String givenName, String surname, List<String> roles,
        Boolean active) {
        this.username = username;
        this.emailAddress = emailAddress;
        this.givenName = givenName;
        this.surname = surname;
        this.roles = new ArrayList<>();
        if (roles != null && !roles.isEmpty()) {
            this.roles.addAll(roles);
        }
        this.active = active != null ? active : true;
    }
}
