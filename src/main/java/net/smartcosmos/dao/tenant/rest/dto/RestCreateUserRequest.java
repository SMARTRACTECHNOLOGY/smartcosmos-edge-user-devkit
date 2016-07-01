package net.smartcosmos.dao.tenant.rest.dto;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
//@ApiModel(description = "Create a \"Tenant\" in the smartcosmos-edge-tenant Server.")
public class RestCreateUserRequest {

    private static final int VERSION = 1;
    @Setter(AccessLevel.NONE)
    private int version = VERSION;

    String tenantUrn;
    String username;
    String emailAddress;
    String givenName;
    String surname;
    List<String> roles;
    List<String> authorities;
    Boolean active;

    @Builder
    @ConstructorProperties({ "urn", "active"})
    public RestCreateUserRequest(String tenantUrn, String username, String emailAddress, String givenName, String surname, List<String> roles,
                                 List<String> authorities, Boolean active) {
        this.tenantUrn = tenantUrn;
        this.username = username;
        this.emailAddress = emailAddress;
        this.givenName = givenName;
        this.surname = surname;
        this.roles = roles;
        this.authorities = authorities;

        this.active = active != null ? active : true;

        this.version = VERSION;
    }
}
