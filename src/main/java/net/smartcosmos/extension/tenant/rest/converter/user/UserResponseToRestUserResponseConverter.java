package net.smartcosmos.extension.tenant.rest.converter.user;

import net.smartcosmos.extension.tenant.dto.user.UserResponse;
import net.smartcosmos.extension.tenant.rest.dto.user.RestUserResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class UserResponseToRestUserResponseConverter
    implements Converter<UserResponse, RestUserResponse>, FormatterRegistrar {

    @Override
    public RestUserResponse convert(UserResponse user) {
        return RestUserResponse.builder()
                .urn(user.getUrn())
                .username(user.getUsername())
                .emailAddress(user.getEmailAddress())
                .givenName(user.getGivenName())
                .surname(user.getSurname())
                .roles(user.getRoles())
                .active(user.getActive())
                .tenantUrn(user.getTenantUrn())
                .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}