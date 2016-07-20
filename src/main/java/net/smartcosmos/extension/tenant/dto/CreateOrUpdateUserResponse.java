package net.smartcosmos.extension.tenant.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
@Builder
@AllArgsConstructor
public class CreateOrUpdateUserResponse {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private final String urn;
    private final String tenantUrn;
    private final String username;
    private final String emailAddress;
    private final String givenName;
    private final String surname;
    private final String password;
    private final List<String> roles;
    private final Boolean active;
}
