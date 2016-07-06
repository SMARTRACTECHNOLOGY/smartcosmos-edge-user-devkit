package net.smartcosmos.ext.tenant.rest.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.ext.tenant.dto.CreateUserResponse;
import net.smartcosmos.ext.tenant.rest.dto.RestCreateUserResponse;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class CreateUserResponseToRestCreateUserResponseConverter
    implements Converter<CreateUserResponse, RestCreateUserResponse>, FormatterRegistrar {

    @Override
    public RestCreateUserResponse convert(CreateUserResponse createUserResponse) {
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
