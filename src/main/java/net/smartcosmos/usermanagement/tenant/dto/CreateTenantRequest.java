package net.smartcosmos.usermanagement.tenant.dto;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateTenantRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private String name;
    private String username;
    private Boolean active;

    @Builder
    @ConstructorProperties({ "name", "username", "active" })
    public CreateTenantRequest(String name, String username, Boolean active) {

        this.name = name;
        this.username = username;
        this.active = active != null ? active : true;
    }

}
