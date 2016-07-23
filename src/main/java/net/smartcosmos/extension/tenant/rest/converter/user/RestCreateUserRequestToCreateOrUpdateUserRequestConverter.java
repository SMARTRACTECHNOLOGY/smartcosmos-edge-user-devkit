package net.smartcosmos.extension.tenant.rest.converter.user;

import net.smartcosmos.extension.tenant.dto.user.CreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateUserRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class RestCreateUserRequestToCreateOrUpdateUserRequestConverter
    implements Converter<RestCreateUserRequest, CreateOrUpdateUserRequest>, FormatterRegistrar {

    @Override
    public CreateOrUpdateUserRequest convert(RestCreateUserRequest restCreateUserRequest) {
        return CreateOrUpdateUserRequest.builder()
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
