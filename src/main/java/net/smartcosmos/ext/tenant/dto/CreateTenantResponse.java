package net.smartcosmos.ext.tenant.dto;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
public class CreateTenantResponse {

    private static final int VERSION = 1;
    @Setter(AccessLevel.NONE)
    private int version = VERSION;

    private final String urn;
    private final String name;
    private final Boolean active;

    private final CreateOrUpdateUserResponse admin;

    @Builder
    @ConstructorProperties({ "urn", "name", "tenantUrn", "active", "admin" })
    public CreateTenantResponse(String urn, String name, Boolean active, CreateOrUpdateUserResponse admin) {
        this.urn = urn;
        this.name = name;
        this.active = active;
        this.admin = admin;

        this.version = VERSION;
    }
}
