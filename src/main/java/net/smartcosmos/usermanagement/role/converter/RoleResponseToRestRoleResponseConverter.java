package net.smartcosmos.usermanagement.role.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.usermanagement.role.dto.RestRoleResponse;
import net.smartcosmos.usermanagement.role.dto.RoleResponse;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class RoleResponseToRestRoleResponseConverter
    implements Converter<RoleResponse, RestRoleResponse>, FormatterRegistrar {

    @Override
    public RestRoleResponse convert(RoleResponse role) {

        return RestRoleResponse.builder()
            .urn(role.getUrn())
            .name(role.getName())
            .active(role.getActive())
            .authorities(role.getAuthorities())
            .tenantUrn(role.getTenantUrn())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
