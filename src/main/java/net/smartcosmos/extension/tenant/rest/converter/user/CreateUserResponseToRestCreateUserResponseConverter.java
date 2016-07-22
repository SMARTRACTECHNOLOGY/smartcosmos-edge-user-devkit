package net.smartcosmos.extension.tenant.rest.converter.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.dto.user.CreateOrUpdateUserResponse;
import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateUserResponse;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class CreateUserResponseToRestCreateUserResponseConverter
    implements Converter<CreateOrUpdateUserResponse, RestCreateUserResponse>, FormatterRegistrar {

    @Override
    public RestCreateUserResponse convert(CreateOrUpdateUserResponse createUserResponse) {
        return RestCreateUserResponse.builder()
            .urn(createUserResponse.getUrn())
            .tenantUrn(createUserResponse.getTenantUrn())
            .username(createUserResponse.getUsername())
            .emailAddress(createUserResponse.getEmailAddress())
            .givenName(createUserResponse.getGivenName())
            .surname(createUserResponse.getSurname())
            .roles(createUserResponse.getRoles())
            .active(createUserResponse.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
