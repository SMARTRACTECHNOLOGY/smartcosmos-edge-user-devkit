package net.smartcosmos.usermanagement.tenant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.usermanagement.tenant.domain.TenantEntity;

/**
 * Details for a Tenant with a corresponding user.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
@Builder
@AllArgsConstructor
public class TenantEntityAndUserEntityDto {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private TenantEntity tenantEntity;
    private UserEntity userEntity;
    private String rawPassword;
}
