package net.smartcosmos.extension.tenant.dto;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
public class UpdateTenantRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private String urn;
    private String name;
    private Boolean active;

    @Builder
    @ConstructorProperties({ "urn", "name", "active" })
    public UpdateTenantRequest(String urn, String name, Boolean active) {
        this.urn = urn;
        this.name = name;
        this.active = active != null ? active : true;
    }

}
