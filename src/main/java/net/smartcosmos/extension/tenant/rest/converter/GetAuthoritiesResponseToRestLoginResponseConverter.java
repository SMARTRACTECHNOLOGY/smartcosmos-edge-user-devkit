package net.smartcosmos.extension.tenant.rest.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.dto.GetAuthoritiesResponse;
import net.smartcosmos.extension.tenant.rest.dto.RestLoginResponse;

@Component
public class GetAuthoritiesResponseToRestLoginResponseConverter implements Converter<GetAuthoritiesResponse, RestLoginResponse>, FormatterRegistrar {

    @Override
    public RestLoginResponse convert(GetAuthoritiesResponse source) {

        return RestLoginResponse.builder()
            .urn(source.getUrn())
            .username(source.getUsername())
            .tenantUrn(source.getTenantUrn())
            .authorities(source.getAuthorities())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(this);
    }
}
