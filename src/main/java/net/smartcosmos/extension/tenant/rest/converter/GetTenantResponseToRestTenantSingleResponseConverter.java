package net.smartcosmos.extension.tenant.rest.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.dto.GetTenantResponse;
import net.smartcosmos.extension.tenant.rest.dto.RestTenantSingleResponse;

@Component
public class GetTenantResponseToRestTenantSingleResponseConverter
    implements Converter<GetTenantResponse, RestTenantSingleResponse>, FormatterRegistrar {

    @Override
    public RestTenantSingleResponse convert(GetTenantResponse entity) {
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
