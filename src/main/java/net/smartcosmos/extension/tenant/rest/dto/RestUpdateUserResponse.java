package net.smartcosmos.extension.tenant.rest.dto;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
public class RestUpdateUserResponse {

    private static final int VERSION = 1;
    @Setter(AccessLevel.NONE)
    private int version = VERSION;

    private final String username;
    private final String emailAddress;
    private final String givenName;
    private final String surname;
    private final List<String> roles;
    private final Boolean active;

    @Builder
    @ConstructorProperties({ "username", "emailAddress", "givenName", "surname", "roles", "active" })
    public RestUpdateUserResponse(
        String urn, String tenantUrn, String username, String emailAddress, String givenName, String surname, List<String> roles, Boolean active) {
        this.username = username;
        this.emailAddress = emailAddress;
        this.givenName = givenName;
        this.surname = surname;
        this.roles = roles;
        this.active = active;

        this.version = VERSION;
    }
}
