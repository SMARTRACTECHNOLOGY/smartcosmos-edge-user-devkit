package net.smartcosmos.ext.tenant.dto;

import java.beans.ConstructorProperties;
import java.util.List;

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
public class CreateRoleRequest {

    private static final int VERSION = 1;
    @Setter(AccessLevel.NONE)
    private int version = VERSION;

    private String role;
    private List<String> authorities;
    private Boolean active;

    @Builder
    @ConstructorProperties({ "name", "authorities", "active"})
    public CreateRoleRequest(String name, List<String> authorities, Boolean active)
    {
        this.role = role;
        this.authorities = authorities;
        this.active = active != null ? active : true;

        this.version = VERSION;
    }

}
