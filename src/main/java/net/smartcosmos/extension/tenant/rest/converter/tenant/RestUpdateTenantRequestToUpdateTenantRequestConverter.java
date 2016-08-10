package net.smartcosmos.extension.tenant.rest.converter.tenant;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.dto.tenant.UpdateTenantRequest;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestUpdateTenantRequest;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class RestUpdateTenantRequestToUpdateTenantRequestConverter
    implements Converter<RestUpdateTenantRequest, UpdateTenantRequest>, FormatterRegistrar {

    @Override
    public UpdateTenantRequest convert(RestUpdateTenantRequest restUpdateTenantRequest) {

        return UpdateTenantRequest.builder()
            .name(restUpdateTenantRequest.getName())
            .active(restUpdateTenantRequest.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
