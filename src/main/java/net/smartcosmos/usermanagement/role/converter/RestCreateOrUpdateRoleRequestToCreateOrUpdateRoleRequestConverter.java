package net.smartcosmos.usermanagement.role.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.usermanagement.role.dto.CreateOrUpdateRoleRequest;
import net.smartcosmos.usermanagement.role.dto.RestCreateOrUpdateRoleRequest;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class RestCreateOrUpdateRoleRequestToCreateOrUpdateRoleRequestConverter
    implements Converter<RestCreateOrUpdateRoleRequest, CreateOrUpdateRoleRequest>, FormatterRegistrar {

    @Override
    public CreateOrUpdateRoleRequest convert(RestCreateOrUpdateRoleRequest request) {

        return CreateOrUpdateRoleRequest.builder()
            .name(request.getName())
            .active(request.getActive())
            .authorities(request.getAuthorities())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}