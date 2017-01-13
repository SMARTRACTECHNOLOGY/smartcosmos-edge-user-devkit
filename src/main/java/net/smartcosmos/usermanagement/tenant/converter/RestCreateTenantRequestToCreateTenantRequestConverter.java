package net.smartcosmos.usermanagement.tenant.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.usermanagement.tenant.dto.CreateTenantRequest;
import net.smartcosmos.usermanagement.tenant.dto.RestCreateTenantRequest;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class RestCreateTenantRequestToCreateTenantRequestConverter
    implements Converter<RestCreateTenantRequest, CreateTenantRequest>, FormatterRegistrar {

    @Override
    public CreateTenantRequest convert(RestCreateTenantRequest restCreateTenantRequest) {

        return CreateTenantRequest.builder()
            .name(restCreateTenantRequest.getName())
            .active(restCreateTenantRequest.getActive())
            .username(restCreateTenantRequest.getUsername())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
