package net.smartcosmos.extension.tenant.rest.converter.tenant;

import net.smartcosmos.extension.tenant.dto.tenant.TenantResponse;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestTenantSingleResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TenantResponseToRestTenantSingleResponseConverter implements Converter<TenantResponse, RestTenantSingleResponse>, FormatterRegistrar {


    @Override
    public RestTenantSingleResponse convert(TenantResponse tenantResponse) {
        return RestTenantSingleResponse.builder()
                .active(tenantResponse.getActive())
                .name(tenantResponse.getName())
                .urn(tenantResponse.getUrn())
                .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
