package net.smartcosmos.ext.tenant.converter;

import net.smartcosmos.ext.tenant.domain.TenantEntity;
import net.smartcosmos.ext.tenant.dto.UpdateTenantResponse;
import net.smartcosmos.ext.tenant.util.UuidUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;


@Component
public class TenantEntityToUpdateTenantResponseConverter
        implements Converter<TenantEntity, UpdateTenantResponse>, FormatterRegistrar {

    @Override
    public UpdateTenantResponse convert(TenantEntity entity) {
        return UpdateTenantResponse.builder()
                .active(entity.getActive())
                .name(entity.getName())
                .urn(UuidUtil.getTenantUrnFromUuid(entity.getId()))
                .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(this);
    }
}
