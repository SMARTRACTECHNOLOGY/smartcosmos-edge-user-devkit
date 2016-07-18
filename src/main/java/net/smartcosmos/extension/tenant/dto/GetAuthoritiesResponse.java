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
@Builder
public class GetAuthoritiesResponse {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private final String urn;
    private final String username;
    private final List<String> authorities;
    private final String tenantUrn;

    @ConstructorProperties({ "urn", "username", "authorities", "tenantUrn" })
    public GetAuthoritiesResponse(String urn, String username, List<String> authorities, String tenantUrn) {
        this.urn = urn;
        this.username = username;
        this.authorities = new ArrayList<>();
        if (authorities != null && !authorities.isEmpty()) {
            this.authorities.addAll(authorities);
        }
        this.tenantUrn = tenantUrn;
    }
}
