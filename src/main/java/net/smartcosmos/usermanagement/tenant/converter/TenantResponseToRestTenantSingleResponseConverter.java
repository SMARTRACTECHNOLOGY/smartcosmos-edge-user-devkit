package net.smartcosmos.usermanagement.tenant.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.usermanagement.tenant.dto.RestTenantResponse;
import net.smartcosmos.usermanagement.tenant.dto.TenantResponse;

@Component
public class TenantResponseToRestTenantSingleResponseConverter implements Converter<TenantResponse, RestTenantResponse>, FormatterRegistrar {

    @Override
    public RestTenantResponse convert(TenantResponse tenantResponse) {

        return RestTenantResponse.builder()
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
