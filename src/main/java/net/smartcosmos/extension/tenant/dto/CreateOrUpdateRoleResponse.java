package net.smartcosmos.extension.tenant.dto;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
public class CreateOrUpdateRoleResponse {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private final String urn;
    private final String name;
    private List<String> authorities;
    private final Boolean active;

    @Builder
    @ConstructorProperties({ "urn", "name", "authorities", "active" })
    public CreateOrUpdateRoleResponse(String urn, String name, List<String> authorities, Boolean active) {
        this.urn = urn;
        this.name = name;
        this.authorities = new ArrayList<>();
        if (authorities != null && !authorities.isEmpty()) {
            this.authorities.addAll(authorities);
        }
        this.active = active;
    }
}
