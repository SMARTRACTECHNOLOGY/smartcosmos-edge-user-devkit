package net.smartcosmos.ext.tenant.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import net.smartcosmos.ext.tenant.dao.TenantDao;
import net.smartcosmos.ext.tenant.domain.AuthorityEntity;
import net.smartcosmos.ext.tenant.domain.RoleEntity;
import net.smartcosmos.ext.tenant.domain.TenantEntity;
import net.smartcosmos.ext.tenant.domain.UserEntity;
import net.smartcosmos.ext.tenant.dto.CreateTenantRequest;
import net.smartcosmos.ext.tenant.dto.CreateTenantResponse;
import net.smartcosmos.ext.tenant.dto.CreateUserRequest;
import net.smartcosmos.ext.tenant.dto.CreateUserResponse;
import net.smartcosmos.ext.tenant.dto.GetTenantResponse;
import net.smartcosmos.ext.tenant.dto.GetUserResponse;
import net.smartcosmos.ext.tenant.dto.TenantEntityAndUserEntityDto;
import net.smartcosmos.ext.tenant.repository.RoleRepository;
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
    private final RoleRepository roleRepository;
    private final ConversionService conversionService;

    @Autowired
    public TenantPersistenceService(
        TenantRepository tenantRepository,
        UserRepository userRepository,
        RoleRepository roleRepository,
        ConversionService conversionService) {

        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.conversionService = conversionService;
    }

    @Override
    public Optional<CreateTenantResponse> createTenant(CreateTenantRequest createTenantRequest)
        throws ConstraintViolationException {

        // This tenant already exists? we're not creating a new one
        if (tenantRepository.findByName(createTenantRequest.getName())
            .isPresent()) {
            return Optional.empty();
        }

        Set<RoleEntity> roles = new HashSet<>();
        // this creates the Admin role if it doesn't already exist.
        roles.add(getAdminRole());
        // we also create the User role if it doesn't already exist, but don't give it to the default admin user.
        getUserRole();

        TenantEntity tenantEntity = tenantRepository.save(conversionService.convert(createTenantRequest, TenantEntity.class));

        UserEntity userEntity = UserEntity.builder()
            .id(UuidUtil.getNewUuid())
            .tenantId(tenantEntity.getId())
            .username(createTenantRequest.getUsername())
            .emailAddress(createTenantRequest.getUsername())
            .password("PleaseChangeMeImmediately")
            .roles(roles)
            .active(createTenantRequest.getActive() == null ? true : createTenantRequest.getActive())
            .build();

        userEntity = userRepository.save(userEntity);

        return Optional
            .ofNullable(conversionService.convert(TenantEntityAndUserEntityDto.builder()
                                                      .tenantEntity(tenantEntity)
                                                      .userEntity(userEntity)
                                                      .build(),
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

        // This user already exists? We're not creating a new one.
        if (userRepository.findByUsernameAndTenantId(
            createUserRequest.getUsername(),
            UuidUtil.getUuidFromUrn(createUserRequest.getTenantUrn()))
            .isPresent()) {

            return Optional.empty();
        }

        UserEntity userEntity = userRepository.save(conversionService.convert(createUserRequest, UserEntity.class));
        return Optional.ofNullable(conversionService.convert(userEntity, CreateUserResponse.class));
    }

    @Override
    public Optional<GetUserResponse> findUserByUrn(String userUrn) {

        if (userUrn == null || userUrn.isEmpty()) {
            return Optional.empty();
        }

        try {
            UUID id = UuidUtil.getUuidFromUrn(userUrn);
            Optional<UserEntity> entity = userRepository.findById(id);
            if (entity.isPresent()) {
                final GetUserResponse response = conversionService.convert(entity.get(), GetUserResponse.class);
                return Optional.ofNullable(response);
            }
            return Optional.empty();

        } catch (IllegalArgumentException | ConversionException e) {
            String msg = String.format("findByUrn failed, user: '%s', cause: %s", userUrn, e.toString());
            log.error(msg);
            log.debug(msg, e);
            throw e;
        }
    }

    private RoleEntity getAdminRole() {
        RoleEntity adminRole;
        Optional<RoleEntity> optionalAdminRole = roleRepository.findByName("Admin");
        if (optionalAdminRole.isPresent()) {
            adminRole = optionalAdminRole.get();
        } else {
            Set<AuthorityEntity> authorities = new HashSet<>();
            authorities.add(AuthorityEntity.builder()
                                .authority("smartcosmos.things.read")
                                .build());
            authorities.add(AuthorityEntity.builder()
                                .authority("smartcosmos.things.write")
                                .build());
            adminRole = roleRepository.save(RoleEntity.builder()
                                                .name("Admin")
                                                .authorities(authorities)
                                                .active(true)
                                                .build());
        } return adminRole;
    }

    private RoleEntity getUserRole() {
        RoleEntity userRole;
        Optional<RoleEntity> optionalAdminRole = roleRepository.findByName("User");
        if (optionalAdminRole.isPresent()) {
            userRole = optionalAdminRole.get();
        } else {
            Set<AuthorityEntity> authorities = new HashSet<>();
            authorities.add(AuthorityEntity.builder()
                                .authority("smartcosmos.things.read")
                                .build());
            userRole = roleRepository.save(RoleEntity.builder()
                                                .name("User")
                                                .authorities(authorities)
                                                .active(true)
                                                .build());
        } return userRole;
    }



}

