package net.smartcosmos.usermanagement.tenant.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import net.smartcosmos.usermanagement.tenant.dto.CreateTenantResponse;
import net.smartcosmos.usermanagement.tenant.dto.RestCreateTenantResponse;
import net.smartcosmos.usermanagement.user.dto.CreateUserResponse;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Component
public class CreateTenantResponseToRestCreateTenantResponseConverter
    implements Converter<CreateTenantResponse, RestCreateTenantResponse> {

    @Override
    public RestCreateTenantResponse convert(CreateTenantResponse createTenantResponse) {

        CreateUserResponse admin = createTenantResponse.getAdmin();

        return RestCreateTenantResponse.builder()
            .urn(createTenantResponse.getUrn())
            .admin(admin)
            .build();
    }
}
