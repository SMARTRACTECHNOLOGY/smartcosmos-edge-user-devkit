package net.smartcosmos.usermanagement.tenant.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import net.smartcosmos.usermanagement.ConversionServiceAwareConverter;
import net.smartcosmos.usermanagement.tenant.dto.CreateTenantResponse;
import net.smartcosmos.usermanagement.tenant.dto.RestCreateTenantResponse;
import net.smartcosmos.usermanagement.user.dto.RestCreateUserResponse;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class CreateTenantResponseToRestCreateTenantResponseConverter
    extends ConversionServiceAwareConverter<CreateTenantResponse, RestCreateTenantResponse> {

    @Autowired
    private ConversionService conversionService;

    protected ConversionService conversionService() {

        return conversionService;
    }

    @Override
    public RestCreateTenantResponse convert(CreateTenantResponse createTenantResponse) {

        RestCreateUserResponse admin = conversionService.convert(createTenantResponse.getAdmin(), RestCreateUserResponse.class);

        return RestCreateTenantResponse.builder()
            .urn(createTenantResponse.getUrn())
            .admin(admin)
            .build();
    }
}
