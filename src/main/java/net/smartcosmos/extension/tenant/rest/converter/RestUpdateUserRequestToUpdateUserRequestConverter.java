package net.smartcosmos.extension.tenant.rest.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.dto.user.UpdateUserRequest;
import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateOrUpdateUserRequest;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class RestUpdateUserRequestToUpdateUserRequestConverter
    implements Converter<RestCreateOrUpdateUserRequest, UpdateUserRequest>, FormatterRegistrar {

    @Override
    public UpdateUserRequest convert(RestCreateOrUpdateUserRequest userRequest) {
        return UpdateUserRequest.builder()
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
