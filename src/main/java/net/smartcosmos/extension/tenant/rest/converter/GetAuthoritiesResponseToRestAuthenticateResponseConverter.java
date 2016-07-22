package net.smartcosmos.extension.tenant.rest.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.dto.authentication.GetAuthoritiesResponse;
import net.smartcosmos.extension.tenant.rest.dto.authentication.RestAuthenticateResponse;

@Component
public class GetAuthoritiesResponseToRestAuthenticateResponseConverter
    implements Converter<GetAuthoritiesResponse, RestAuthenticateResponse>, FormatterRegistrar {

    @Override
    public RestAuthenticateResponse convert(GetAuthoritiesResponse source) {

        return RestAuthenticateResponse.builder()
            .userUrn(source.getUrn())
            .username(source.getUsername())
            .passwordHash(source.getPasswordHash())
            .tenantUrn(source.getTenantUrn())
            .authorities(source.getAuthorities())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(this);
    }
}
