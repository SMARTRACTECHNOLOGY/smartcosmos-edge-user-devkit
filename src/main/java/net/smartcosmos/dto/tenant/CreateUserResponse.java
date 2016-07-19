package net.smartcosmos.dto.tenant;

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
public class CreateUserResponse {

    private static final int VERSION = 1;
    @Setter(AccessLevel.NONE)
    private int version = VERSION;

    private final String urn;
    private final String tenantUrn;
    private final String username;
    private final String emailAddress;
    private final String givenName;
    private final String surname;
    private final String password;
    private final List<String> roles;
    private final List<String> authorities;

    @Builder
    @ConstructorProperties({ "urn", "tenantUrn", "username", "emailAddress", "givenName", "surname", "password", "roles", "authorities" })
    public CreateUserResponse(
        String urn, String tenantUrn, String username, String emailAddress, String givenName, String surname, String password,
        List<String> roles, List<String> authorities) {
        this.urn = urn;
        this.tenantUrn = tenantUrn;
        this.username = username;
        this.emailAddress = emailAddress;
        this.givenName = givenName;
        this.surname = surname;
        this.password = password;
        this.roles = roles;
        this.authorities = authorities;

        this.version = VERSION;
    }
}