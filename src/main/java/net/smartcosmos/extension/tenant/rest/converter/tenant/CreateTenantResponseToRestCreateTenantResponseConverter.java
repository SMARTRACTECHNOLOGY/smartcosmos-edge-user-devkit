package net.smartcosmos.extension.tenant.rest.converter.tenant;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantResponse;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestCreateTenantResponse;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class CreateTenantResponseToRestCreateTenantResponseConverter
    implements Converter<CreateTenantResponse, RestCreateTenantResponse>, FormatterRegistrar {

    @Override
    public RestCreateTenantResponse convert(CreateTenantResponse createTenantResponse) {
        return RestCreateTenantResponse.builder()
            .urn(createTenantResponse.getUrn())
            .name(createTenantResponse.getName())
            .active(createTenantResponse.getActive())
            .admin(createTenantResponse.getAdmin())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
