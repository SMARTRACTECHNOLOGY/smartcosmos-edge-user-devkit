package net.smartcosmos.extension.tenant.rest.converter.user;

import net.smartcosmos.extension.tenant.dto.user.UserPasswordResponse;
import net.smartcosmos.extension.tenant.rest.dto.user.RestUserPasswordResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class UserPasswordResponseToRestUserPasswordResponseConverter
    implements Converter<UserPasswordResponse, RestUserPasswordResponse>, FormatterRegistrar {

    @Override
    public RestUserPasswordResponse convert(UserPasswordResponse user) {
        return RestUserPasswordResponse.builder()
            .urn(user.getUrn())
            .tenantUrn(user.getTenantUrn())
            .username(user.getUsername())
            .roles(user.getRoles())
            .password(user.getPassword())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
