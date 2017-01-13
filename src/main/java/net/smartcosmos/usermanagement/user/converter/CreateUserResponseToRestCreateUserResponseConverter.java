package net.smartcosmos.usermanagement.user.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.usermanagement.user.dto.CreateUserResponse;
import net.smartcosmos.usermanagement.user.dto.RestCreateUserResponse;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class CreateUserResponseToRestCreateUserResponseConverter
    implements Converter<CreateUserResponse, RestCreateUserResponse>, FormatterRegistrar {

    @Override
    public RestCreateUserResponse convert(CreateUserResponse user) {

        return RestCreateUserResponse.builder()
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
