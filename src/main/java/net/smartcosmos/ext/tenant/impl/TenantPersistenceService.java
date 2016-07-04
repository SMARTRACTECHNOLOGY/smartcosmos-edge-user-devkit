package net.smartcosmos.ext.tenant.impl;

import java.util.Optional;
import java.util.UUID;
import javax.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import net.smartcosmos.ext.tenant.dao.TenantDao;
import net.smartcosmos.ext.tenant.domain.TenantEntity;
import net.smartcosmos.ext.tenant.domain.UserEntity;
import net.smartcosmos.ext.tenant.dto.CreateTenantRequest;
import net.smartcosmos.ext.tenant.dto.CreateTenantResponse;
import net.smartcosmos.ext.tenant.dto.CreateUserRequest;
import net.smartcosmos.ext.tenant.dto.CreateUserResponse;
import net.smartcosmos.ext.tenant.dto.GetTenantResponse;
import net.smartcosmos.ext.tenant.dto.TenantEntityAndUserEntityDto;
import net.smartcosmos.ext.tenant.repository.TenantRepository;
import net.smartcosmos.ext.tenant.repository.UserRepository;
import net.smartcosmos.ext.tenant.util.UuidUtil;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Slf4j
@Service
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
    public Optional<GetTenantResponse> findTenantByUrn(String tenantUrn) {

        if (tenantUrn == null || tenantUrn.isEmpty()) {
            return Optional.empty();
        }

        try {
            UUID id = UuidUtil.getUuidFromUrn(tenantUrn);
            Optional<TenantEntity> entity = tenantRepository.findById(id);
            if (entity.isPresent()) {
                final GetTenantResponse response = conversionService.convert(entity.get(), GetTenantResponse.class);
                return Optional.ofNullable(response);
            }
            return Optional.empty();

        } catch (IllegalArgumentException | ConversionException e) {
            String msg = String.format("findByUrn failed, tenant: '%s', cause: %s", tenantUrn, e.toString());
            log.error(msg);
            log.debug(msg, e);
            throw e;
        }
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

