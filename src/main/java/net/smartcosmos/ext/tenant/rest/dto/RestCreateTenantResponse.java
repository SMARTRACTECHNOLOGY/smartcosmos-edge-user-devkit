package net.smartcosmos.ext.tenant.rest.dto;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import net.smartcosmos.ext.tenant.dto.CreateOrUpdateUserResponse;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
public class RestCreateTenantResponse {

    private static final int VERSION = 1;
    @Setter(AccessLevel.NONE)
    private int version = VERSION;

    private final String urn;
    private final String name;
    private final Boolean active;

    private final CreateOrUpdateUserResponse admin;

    @Builder
    @ConstructorProperties({ "urn", "name", "tenantUrn", "active", "admin" })
    public RestCreateTenantResponse(String urn, String name, Boolean active, CreateOrUpdateUserResponse admin) {
        this.urn = urn;
        this.name = name;
        this.active = active;
        this.admin = admin;

        this.version = VERSION;
    }
}
