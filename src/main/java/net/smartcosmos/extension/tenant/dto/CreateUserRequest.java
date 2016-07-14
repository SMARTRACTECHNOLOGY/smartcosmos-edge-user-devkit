package net.smartcosmos.extension.tenant.dto;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
public class CreateUserRequest {

    private static final int VERSION = 1;
    @Setter(AccessLevel.NONE)
    private int version = VERSION;

    private String username;
    private String tenantUrn;
    private String emailAddress;
    private String givenName;
    private String surname;
    private List<String> roles;
    private List<String> authorities;

    private Boolean active;

    @Builder
    @ConstructorProperties({ "username", "tenantUrn", "emailAddress", "givenName", "surname", "roles", "active" })
    public CreateUserRequest(
        String username, String tenantUrn, String emailAddress, String givenName, String surname, List<String> roles, Boolean active) {

        this.tenantUrn = tenantUrn;
        this.username = username;
        this.emailAddress = emailAddress;
        this.givenName = givenName;
        this.surname = surname;
        this.roles = roles;

        this.active = active != null ? active : true;

        this.version = VERSION;
    }

}
