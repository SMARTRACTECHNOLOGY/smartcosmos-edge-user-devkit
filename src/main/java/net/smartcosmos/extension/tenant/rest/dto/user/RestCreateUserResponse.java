package net.smartcosmos.extension.tenant.rest.dto.user;

import java.util.Set;

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
public class RestCreateUserResponse {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private final String urn;
    private final String username;
    private final String password;
    private final Set<String> roles;
    private final String tenantUrn;
}
