package net.smartcosmos.extension.tenant.dto;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import net.smartcosmos.extension.tenant.domain.TenantEntity;
import net.smartcosmos.extension.tenant.domain.UserEntity;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
public class TenantEntityAndUserEntityDto {

    private static final int VERSION = 1;
    @Setter(AccessLevel.NONE)
    private int version = VERSION;

    private TenantEntity tenantEntity;
    private UserEntity userEntity;

    @Builder
    @ConstructorProperties({ "tenantEntity", "userEntity" })
    public TenantEntityAndUserEntityDto(TenantEntity tenantEntity, UserEntity userEntity) {

        this.tenantEntity = tenantEntity;
        this.userEntity = userEntity;

        this.version = VERSION;
    }
}
