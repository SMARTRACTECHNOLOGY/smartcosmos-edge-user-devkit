package net.smartcosmos.usermanagement.role.dto;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private String name;
    private Set<String> authorities;
    private Boolean active;

    @Builder
    @ConstructorProperties({ "name", "authorities", "active" })
    public RoleRequest(String name, Collection<String> authorities, Boolean active) {

        this.name = name;
        this.authorities = new HashSet<>();
        if (authorities != null && !authorities.isEmpty()) {
            this.authorities.addAll(authorities);
        }
        this.active = active;
    }

}
