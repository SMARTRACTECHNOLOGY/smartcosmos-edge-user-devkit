package net.smartcosmos.extension.tenant.rest.dto.role;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
public class RestRoleResponse {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private final String urn;
    private final String name;
    private final Set<String> authorities;
    private final Boolean active;
    private final String tenantUrn;

    @Builder
    @ConstructorProperties({ "urn", "name", "authorities", "active", "tenantUrn" })
    public RestRoleResponse(String urn, String name, Collection<String> authorities, Boolean active, String tenantUrn) {
        this.urn = urn;
        this.name = name;
        this.authorities = new HashSet<>();
        if (authorities != null && !authorities.isEmpty()) {
            this.authorities.addAll(authorities);
        }
        this.active = active;
        this.tenantUrn = tenantUrn;
    }
}
