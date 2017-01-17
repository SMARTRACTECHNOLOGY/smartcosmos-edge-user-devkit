package net.smartcosmos.usermanagement.tenant.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.cluster.userdetails.util.UuidUtil;
import net.smartcosmos.usermanagement.tenant.domain.TenantEntity;
import net.smartcosmos.usermanagement.tenant.dto.CreateTenantRequest;

/**
 * Convert {@link CreateTenantRequest}s to {@link TenantEntity}s.
 */
@Component
public class CreateTenantRequestToTenantEntityConverter
    implements Converter<CreateTenantRequest, TenantEntity>, FormatterRegistrar {

    @Override
    public TenantEntity convert(CreateTenantRequest createTenantRequest) {

        return TenantEntity.builder()
            .id(UuidUtil.getNewUuid())
            .name(createTenantRequest.getName())
            .active(createTenantRequest.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
