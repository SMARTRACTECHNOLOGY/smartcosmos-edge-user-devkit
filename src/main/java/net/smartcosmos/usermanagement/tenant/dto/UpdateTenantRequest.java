package net.smartcosmos.usermanagement.tenant.dto;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Details for updating a Tenant.
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateTenantRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private String name;
    private Boolean active;

    @Builder
    @ConstructorProperties({ "name", "active" })
    public UpdateTenantRequest(String name, Boolean active) {

        this.name = name;
        this.active = active != null ? active : true;
    }

}
