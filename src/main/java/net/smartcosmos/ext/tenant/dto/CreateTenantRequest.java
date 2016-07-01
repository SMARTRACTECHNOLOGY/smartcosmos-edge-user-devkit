package net.smartcosmos.ext.tenant.dto;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
public class CreateTenantRequest {

    private static final int VERSION = 1;
    @Setter(AccessLevel.NONE)
    private int version = VERSION;

    private String name;
    private String username;
    private String emailAddress;
    private Boolean active;

    @Builder
    @ConstructorProperties({ "urn", "type", "active"})
    public CreateTenantRequest(String name, String username, String emailAddress, Boolean active)
    {
        this.name = name;
        this.username = username;
        this.emailAddress = emailAddress;
        this.active = active != null ? active : true;

        this.version = VERSION;
    }

}
