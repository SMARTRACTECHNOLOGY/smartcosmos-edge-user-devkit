package net.smartcosmos.extension.tenant.dto;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

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
public class CreateOrUpdateRoleRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private String name;
    private List<String> authorities;
    private Boolean active;

    @Builder
    @ConstructorProperties({ "name", "authorities", "active" })
    public CreateOrUpdateRoleRequest(String name, List<String> authorities, Boolean active) {
        this.name = name;
        this.authorities = new ArrayList<>();
        if (authorities != null && !authorities.isEmpty()) {
            this.authorities.addAll(authorities);
        }
        this.active = active != null ? active : true;
    }

}
