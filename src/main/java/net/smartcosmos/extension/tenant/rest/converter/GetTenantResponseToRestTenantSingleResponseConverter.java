package net.smartcosmos.extension.tenant.rest.converter;

import net.smartcosmos.extension.tenant.dto.GetTenantResponse;
import net.smartcosmos.extension.tenant.rest.dto.RestTenantSingleResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;


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
