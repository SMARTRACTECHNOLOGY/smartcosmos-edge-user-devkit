package net.smartcosmos.extension.tenant.rest.converter;

import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.rest.dto.RestCreateOrUpdateRoleRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.rest.dto.RestCreateOrUpdateRoleRequest;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class RestCreateOrUpdateRoleRequestToCreateOrUpdateRoleRequestConverter
    implements Converter<RestCreateOrUpdateRoleRequest, CreateOrUpdateRoleRequest>, FormatterRegistrar {

        @Override
        public CreateOrUpdateRoleRequest convert(RestCreateOrUpdateRoleRequest restCreateOrUpdateRoleRequest) {
        return CreateOrUpdateRoleRequest.builder()
            .name(restCreateOrUpdateRoleRequest.getName())
            .active(restCreateOrUpdateRoleRequest.getActive())
            .authorities(restCreateOrUpdateRoleRequest.getAuthorities())
            .build();
    }

        @Override
        public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
