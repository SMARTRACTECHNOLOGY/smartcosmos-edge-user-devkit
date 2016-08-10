package net.smartcosmos.extension.tenant.rest.converter.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.dto.user.CreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateOrUpdateUserRequest;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class RestCreateOrUpdateUserRequestToCreateOrUpdateUserRequestConverter
    implements Converter<RestCreateOrUpdateUserRequest, CreateOrUpdateUserRequest>, FormatterRegistrar {

    @Override
    public CreateOrUpdateUserRequest convert(RestCreateOrUpdateUserRequest userRequest) {

        return CreateOrUpdateUserRequest.builder()
            .username(userRequest.getUsername())
            .emailAddress(userRequest.getEmailAddress())
            .givenName(userRequest.getGivenName())
            .surname(userRequest.getSurname())
            .roles(userRequest.getRoles())
            .active(userRequest.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
