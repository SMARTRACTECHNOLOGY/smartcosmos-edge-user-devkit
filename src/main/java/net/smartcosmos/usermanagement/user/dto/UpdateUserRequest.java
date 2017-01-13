package net.smartcosmos.usermanagement.user.dto;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
public class UpdateUserRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private String username;
    private String emailAddress;
    private String givenName;
    private String surname;
    private String password;
    private Set<String> roles;
    private Boolean active;

    @Builder
    @ConstructorProperties({ "username", "emailAddress", "givenName", "surname", "password", "roles", "active" })
    public UpdateUserRequest(
        String username, String emailAddress, String givenName, String surname, String password, Collection<String> roles, Boolean active) {

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
