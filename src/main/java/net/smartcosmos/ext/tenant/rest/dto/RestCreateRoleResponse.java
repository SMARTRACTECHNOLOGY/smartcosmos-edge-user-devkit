package net.smartcosmos.ext.tenant.rest.dto;

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
public class RestCreateRoleResponse {

    private static final int VERSION = 1;
    @Setter(AccessLevel.NONE)
    private int version = VERSION;

    private final String name;
    private final List<String> authorities;
    private final Boolean active;

    @Builder
    @ConstructorProperties({"name", "authorities", "active"})
    public RestCreateRoleResponse(String name, List<String> authorities, Boolean active) {
        this.name = name;
        this.authorities = authorities;
        this.active = active;

        this.version = VERSION;
    }
}
