package net.smartcosmos.extension.tenant.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.beans.ConstructorProperties;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
public class RestTenantSingleResponse {

    private static final int VERSION = 1;
    @Setter(AccessLevel.NONE)
    private int version = VERSION;

    private final String urn;
    private final String name;
    private final Boolean active;

    @Builder
    @ConstructorProperties({ "urn", "name", "active" })
    public RestTenantSingleResponse(String urn, String name, Boolean active) {
        this.urn = urn;
        this.name = name;
        this.active = active;

        this.version = VERSION;
    }
}
