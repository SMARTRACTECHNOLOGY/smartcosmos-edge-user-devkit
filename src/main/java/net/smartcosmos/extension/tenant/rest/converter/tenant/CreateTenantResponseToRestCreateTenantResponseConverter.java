package net.smartcosmos.extension.tenant.rest.converter.tenant;

import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantResponse;
import net.smartcosmos.extension.tenant.rest.converter.ConversionServiceAwareConverter;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestCreateTenantResponse;
import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateUserResponse;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class CreateTenantResponseToRestCreateTenantResponseConverter
    extends ConversionServiceAwareConverter<CreateTenantResponse, RestCreateTenantResponse> {

    @Inject
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
