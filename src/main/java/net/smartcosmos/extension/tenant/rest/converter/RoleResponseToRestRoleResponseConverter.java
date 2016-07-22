package net.smartcosmos.extension.tenant.rest.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.dto.role.RoleResponse;
import net.smartcosmos.extension.tenant.rest.dto.role.RestRoleResponse;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class RoleResponseToRestRoleResponseConverter
    implements Converter<RoleResponse, RestRoleResponse>, FormatterRegistrar {

    @Override
    public RestRoleResponse convert(RoleResponse createIOrUpdateRoleResponse) {
        return RestRoleResponse.builder()
            .urn(createIOrUpdateRoleResponse.getUrn())
            .name(createIOrUpdateRoleResponse.getName())
            .active(createIOrUpdateRoleResponse.getActive())
            .authorities(createIOrUpdateRoleResponse.getAuthorities())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
