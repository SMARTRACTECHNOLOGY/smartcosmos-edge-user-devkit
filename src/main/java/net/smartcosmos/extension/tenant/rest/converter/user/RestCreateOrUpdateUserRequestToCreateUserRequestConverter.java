package net.smartcosmos.extension.tenant.rest.converter.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.dto.user.CreateUserRequest;
import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateOrUpdateUserRequest;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class RestCreateOrUpdateUserRequestToCreateUserRequestConverter
    implements Converter<RestCreateOrUpdateUserRequest, CreateUserRequest>, FormatterRegistrar {

    @Override
    public CreateUserRequest convert(RestCreateOrUpdateUserRequest restCreateUserRequest) {
        return CreateUserRequest.builder()
            .username(restCreateUserRequest.getUsername())
            .emailAddress(restCreateUserRequest.getEmailAddress())
            .givenName(restCreateUserRequest.getGivenName())
            .surname(restCreateUserRequest.getSurname())
            .roles(restCreateUserRequest.getRoles())
            .active(restCreateUserRequest.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
