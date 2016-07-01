package net.smartcosmos.ext.tenant.impl;

import java.util.Optional;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import net.smartcosmos.ext.tenant.domain.UserEntity;
import net.smartcosmos.ext.tenant.dao.TenantDao;
import net.smartcosmos.ext.tenant.domain.TenantEntity;
import net.smartcosmos.ext.tenant.repository.TenantRepository;
import net.smartcosmos.ext.tenant.repository.UserRepository;
import net.smartcosmos.ext.tenant.util.UuidUtil;
import net.smartcosmos.ext.tenant.dto.CreateTenantRequest;
import net.smartcosmos.ext.tenant.dto.CreateTenantResponse;
import net.smartcosmos.ext.tenant.dto.CreateUserRequest;
import net.smartcosmos.ext.tenant.dto.CreateUserResponse;
import net.smartcosmos.ext.tenant.dto.TenantEntityAndUserEntityDto;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public class TenantPersistenceService implements TenantDao {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final ConversionService conversionService;

    @Autowired
    public TenantPersistenceService(
        TenantRepository tenantRepository,
        UserRepository userRepository,
        ConversionService conversionService) {

        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.conversionService = conversionService;
    }

    @Override
    public Optional<CreateTenantResponse> createTenant(CreateTenantRequest createTenantRequest)
        throws ConstraintViolationException {

        if (tenantRepository.findByName(createTenantRequest.getName()).isPresent()) {
            return Optional.empty();
        }

        TenantEntity tenantEntity = conversionService.convert(createTenantRequest, TenantEntity.class);
        tenantEntity = tenantRepository.save(tenantEntity);

        UserEntity userEntity = UserEntity.builder()
            .id(UuidUtil.getNewUuid())
            .tenantId(tenantEntity.getId())
            .username(createTenantRequest.getUsername())
            .emailAddress(createTenantRequest.getEmailAddress())
            .build();

        userEntity = userRepository.save(userEntity);

        return Optional
            .ofNullable(conversionService.convert(TenantEntityAndUserEntityDto.builder().tenantEntity(tenantEntity).userEntity(userEntity).build(),
                                                  CreateTenantResponse.class));
    }

    @Override
    public Optional<CreateTenantResponse> findTenantByUrn(String tenantUrn) {

        Optional<TenantEntity> createTenantResponse = tenantRepository.findById(UuidUtil.getUuidFromUrn(tenantUrn));
        //return conversionService.convert(createTenantResponse, RestCreateTenantResponse.class);
        return null;
    }

    @Override
    public Optional<CreateUserResponse> createUser(CreateUserRequest createUserRequest)
        throws ConstraintViolationException {

        if (userRepository.findByUsernameAndTenantId(
            createUserRequest.getUsername(),
            UuidUtil.getUuidFromUrn(createUserRequest.getTenantUrn())).isPresent()) {

            return Optional.empty();
        }

        return null;
    }
}

