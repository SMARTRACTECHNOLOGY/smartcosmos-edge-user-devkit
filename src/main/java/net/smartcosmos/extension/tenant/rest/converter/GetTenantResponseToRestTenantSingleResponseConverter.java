package net.smartcosmos.extension.tenant.rest.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.dto.tenant.TenantResponse;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestTenantSingleResponse;

@Component
public class GetTenantResponseToRestTenantSingleResponseConverter
    implements Converter<TenantResponse, RestTenantSingleResponse>, FormatterRegistrar {

    @Override
    public RestTenantSingleResponse convert(TenantResponse entity) {
        return RestTenantSingleResponse.builder()
            .urn(entity.getUrn())
            .name(entity.getName())
            .active(entity.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
