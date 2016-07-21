package net.smartcosmos.extension.tenant.rest.dto;

import java.beans.ConstructorProperties;

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
//@ApiModel(description = "Update a \"Tenant\" in the smartcosmos-edge-tenant Server.")
public class RestUpdateTenantRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    String name;
    Boolean active;

    @Builder
    @ConstructorProperties({ "name", "username", "active" })
    public RestUpdateTenantRequest(String name, String username, Boolean active) {
        this.name = name;
        this.active = active != null ? active : true;
    }
}
