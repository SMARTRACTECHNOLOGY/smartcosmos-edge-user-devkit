package net.smartcosmos.extension.tenant.rest.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.dto.UpdateUserRequest;
import net.smartcosmos.extension.tenant.rest.dto.RestUpdateUserRequest;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class RestUpdateUserRequestToUpdateUserRequestConverter
    implements Converter<RestUpdateUserRequest, UpdateUserRequest>, FormatterRegistrar {

    @Override
    public UpdateUserRequest convert(RestUpdateUserRequest restUpdateUserRequest) {
        return UpdateUserRequest.builder()
            .username(restUpdateUserRequest.getUsername())
            .emailAddress(restUpdateUserRequest.getEmailAddress())
            .givenName(restUpdateUserRequest.getGivenName())
            .surname(restUpdateUserRequest.getSurname())
            .roles(restUpdateUserRequest.getRoles())
            .active(restUpdateUserRequest.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
