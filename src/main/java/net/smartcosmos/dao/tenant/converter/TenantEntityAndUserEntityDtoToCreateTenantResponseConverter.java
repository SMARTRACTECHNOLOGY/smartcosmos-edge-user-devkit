package net.smartcosmos.dao.tenant.converter;

import java.util.Arrays;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import net.smartcosmos.dao.tenant.util.UuidUtil;
import net.smartcosmos.dto.tenant.CreateTenantResponse;
import net.smartcosmos.dto.tenant.CreateUserResponse;
import net.smartcosmos.dto.tenant.TenantEntityAndUserEntityDto;
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

        CreateUserResponse userResponse = CreateUserResponse.builder()
            .urn(UuidUtil.getUserUrnFromUuid(entityDto.getUserEntity().getId()))
            .username(entityDto.getUserEntity().getUsername())
            .password(entityDto.getUserEntity().getPassword())
            .roles(Arrays.asList(StringUtils.split(entityDto.getUserEntity().getRoles(), " ")))
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
