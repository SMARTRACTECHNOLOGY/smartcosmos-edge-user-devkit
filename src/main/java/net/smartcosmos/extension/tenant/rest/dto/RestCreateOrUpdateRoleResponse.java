package net.smartcosmos.extension.tenant.rest.dto;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
public class RestCreateOrUpdateRoleResponse {

    private static final int VERSION = 1;
    @Setter(AccessLevel.NONE)
    private int version = VERSION;

    private final String urn;
    private final String name;
    private List<String> authorities;
    private final Boolean active;

    @Builder
    @ConstructorProperties({ "urn", "name", "authorities", "active" })
    public RestCreateOrUpdateRoleResponse(String urn, String name, List<String> authorities, Boolean active) {
        this.urn = urn;
        this.name = name;
        this.authorities = authorities;
        this.active = active;

        this.version = VERSION;
    }
}
