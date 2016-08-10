package net.smartcosmos.extension.tenant.rest.dto.tenant;

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
//@ApiModel(description = "Create a \"Tenant\" in the smartcosmos-edge-tenant Server.")
public class RestCreateTenantRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    String name;
    String username;
    Boolean active;

    @Builder
    @ConstructorProperties({ "name", "username", "active" })
    public RestCreateTenantRequest(String name, String username, Boolean active) {

        this.name = name;
        this.username = username;
        this.active = active != null ? active : true;
    }
}
