package net.smartcosmos.ext.tenant.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.ext.tenant.domain.RoleEntity;
import net.smartcosmos.ext.tenant.dto.CreateTenantResponse;
import net.smartcosmos.ext.tenant.dto.CreateUserResponse;
import net.smartcosmos.ext.tenant.dto.TenantEntityAndUserEntityDto;
import net.smartcosmos.ext.tenant.util.UuidUtil;
/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Component
public class TenantEntityAndUserEntityDtoToCreateTenantResponseConverter
    implements Converter<TenantEntityAndUserEntityDto, CreateTenantResponse>, FormatterRegistrar {

    @Override
    public CreateTenantResponse convert(TenantEntityAndUserEntityDto entityDto) {

        if (entityDto.getTenantEntity() == null || entityDto.getUserEntity() == null) {
            return null;
        }

        List<String> rolesAsStrings = new ArrayList<>();
        for (RoleEntity roleEntity: entityDto.getUserEntity().getRoles()) {
            rolesAsStrings.add(roleEntity.getName());
        }
        CreateUserResponse userResponse = CreateUserResponse.builder()
            .urn(UuidUtil.getUserUrnFromUuid(entityDto.getUserEntity().getId()))
            .tenantUrn(UuidUtil.getUserUrnFromUuid(entityDto.getTenantEntity().getId()))
            .username(entityDto.getUserEntity().getUsername())
            .emailAddress(entityDto.getUserEntity().getEmailAddress())
            .password(entityDto.getUserEntity().getPassword())
            .roles(rolesAsStrings)
            .build();

        return CreateTenantResponse.builder()
            .name(entityDto.getTenantEntity().getName())
            .active(entityDto.getTenantEntity().getActive())
            .urn(UuidUtil.getTenantUrnFromUuid(entityDto.getTenantEntity().getId()))
            .admin(userResponse)
            .build();
    }
    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
