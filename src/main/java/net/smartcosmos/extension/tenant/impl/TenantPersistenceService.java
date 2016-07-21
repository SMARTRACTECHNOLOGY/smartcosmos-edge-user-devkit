package net.smartcosmos.extension.tenant.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.domain.TenantEntity;
import net.smartcosmos.extension.tenant.domain.UserEntity;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleResponse;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateUserResponse;
import net.smartcosmos.extension.tenant.dto.CreateTenantRequest;
import net.smartcosmos.extension.tenant.dto.CreateTenantResponse;
import net.smartcosmos.extension.tenant.dto.CreateUserRequest;
import net.smartcosmos.extension.tenant.dto.GetAuthoritiesResponse;
import net.smartcosmos.extension.tenant.dto.GetOrDeleteUserResponse;
import net.smartcosmos.extension.tenant.dto.GetTenantResponse;
import net.smartcosmos.extension.tenant.dto.TenantEntityAndUserEntityDto;
import net.smartcosmos.extension.tenant.dto.UpdateTenantRequest;
import net.smartcosmos.extension.tenant.dto.UpdateTenantResponse;
import net.smartcosmos.extension.tenant.dto.UpdateUserRequest;
import net.smartcosmos.extension.tenant.repository.RoleRepository;
import net.smartcosmos.extension.tenant.repository.TenantRepository;
import net.smartcosmos.extension.tenant.repository.UserRepository;
import net.smartcosmos.extension.tenant.util.MergeUtil;
import net.smartcosmos.extension.tenant.util.UuidUtil;

import static java.util.stream.Collectors.toSet;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Slf4j
@Service
public class TenantPersistenceService implements TenantDao {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RolePersistenceService rolePersistenceService;
    private final RoleRepository roleRepository;
    private final ConversionService conversionService;

    private static final String INITIAL_PASSWORD = "PleaseChangeMeImmediately";

    /**
     * @param tenantRepository
     * @param userRepository
     * @param rolePersistenceService
     * @param conversionService
     */
    @Autowired
    public TenantPersistenceService(
        TenantRepository tenantRepository,
        UserRepository userRepository,
        RolePersistenceService rolePersistenceService,
        RoleRepository roleRepository,
        ConversionService conversionService) {

        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.rolePersistenceService = rolePersistenceService;
        this.roleRepository = roleRepository;
        this.conversionService = conversionService;
    }

    // region TENANT METHODS */

    /**
     *
     * @param createTenantRequest
     * @return Optional<CreateTenantResponse>
     * @throws ConstraintViolationException
     */
    @Override
    public Optional<CreateTenantResponse> createTenant(CreateTenantRequest createTenantRequest)
        throws ConstraintViolationException {

        try {
            // This tenant already exists? we're not creating a new one
            if (tenantRepository.findByNameIgnoreCase(createTenantRequest.getName())
                .isPresent()) {
                return Optional.empty();
            }

            TenantEntity tenantEntity = tenantRepository.save(conversionService.convert(createTenantRequest, TenantEntity.class));

            // by default we create and Admin and a User role for the new tenant, and assign Admin to the default admin user
            RoleEntity adminRole = createAdminRole(UuidUtil.getTenantUrnFromUuid(tenantEntity.getId()));
            RoleEntity userRole = createUserRole(UuidUtil.getTenantUrnFromUuid(tenantEntity.getId()));

            Set<RoleEntity> roles = new HashSet<>();
            roles.add(adminRole);

            UserEntity userEntity = UserEntity.builder()
                .id(UuidUtil.getNewUuid())
                .tenantId(tenantEntity.getId())
                .username(createTenantRequest.getUsername())
                .emailAddress(createTenantRequest.getUsername())
                .password(INITIAL_PASSWORD)
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

        } catch (IllegalArgumentException | ConversionException e) {
            String msg = String.format("create failed, tenant: '%s', cause: %s", createTenantRequest.getName(), e.toString());
            log.error(msg);
            log.debug(msg, e);
            throw e;
        }
    }

    /**
     * @param updateTenantRequest
     * @return Optional<UpdateTenantResponse>
     * @throws ConstraintViolationException
     */
    @Override
    public Optional<UpdateTenantResponse> updateTenant(String tenantUrn, UpdateTenantRequest updateTenantRequest)
        throws ConstraintViolationException {

        // This tenant already exists? we're not creating a new one
        Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(UuidUtil.getUuidFromUrn(tenantUrn));

        try {
            if (tenantEntityOptional.isPresent()) {
                if (updateTenantRequest.getActive() != null) {
                    tenantEntityOptional.get().setActive(updateTenantRequest.getActive());
                }
                if (updateTenantRequest.getName() != null) {
                    tenantEntityOptional.get().setName(updateTenantRequest.getName());
                }
                TenantEntity tenantEntity = tenantRepository.save(tenantEntityOptional.get());
                return Optional.ofNullable(conversionService.convert(tenantEntity, UpdateTenantResponse.class));
            }
        } catch (IllegalArgumentException | ConstraintViolationException e) {
            String msg = String.format("update failed, tenant: '%s', request: '%s', cause: %s", tenantUrn, updateTenantRequest.toString(), e
                .toString());
            log.error(msg);
            log.debug(msg, e);
            throw e;
        }
        return Optional.empty();
    }

    /**
     * @param tenantUrn
     * @return Optional<GetTenantResponse>
     */
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

    /**
     * @param name
     * @return Optional<GetTenantResponse>
     */
    @Override
    public Optional<GetTenantResponse> findTenantByName(String tenantUrn, String name) {

        UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);
        Optional<TenantEntity> entity = tenantRepository.findByIdAndNameIgnoreCase(tenantId, name);
        if (entity.isPresent()) {
            return Optional.of(conversionService.convert(entity.get(), GetTenantResponse.class));
        }
        return Optional.empty();
    }

    // endregion

    // region USER METHODS

    private boolean userAlreadyExists(String username, UUID tenantId) {
        return userRepository.findByUsernameAndTenantId(username, tenantId).isPresent();
    }

    private boolean userAlreadyExists(String username) {
        return userRepository.findByUsernameIgnoreCase(username).isPresent();
    }

    /**
     *
     * @param createUserRequest
     * @return Optional<CreateOrUpdateUserResponse>
     * @throws ConstraintViolationException
     */
    @Override
    public Optional<CreateOrUpdateUserResponse> createUser(String tenantUrn, CreateUserRequest createUserRequest)
        throws ConstraintViolationException {

        if (userAlreadyExists(createUserRequest.getUsername())) {
            // This user already exists? We're not creating a new one.
            return Optional.empty();
        }

        String password = INITIAL_PASSWORD;

        try {
            UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);

            UserEntity userEntity = conversionService.convert(createUserRequest, UserEntity.class);
            userEntity.setTenantId(tenantId);
            userEntity.setPassword(password);
            userEntity = userRepository.persist(userEntity);
            userEntity = userRepository.addRolesToUser(userEntity.getTenantId(), userEntity.getId(), createUserRequest.getRoles()).get();

            CreateOrUpdateUserResponse response = conversionService.convert(userEntity, CreateOrUpdateUserResponse.class);
            response.setPassword(password);

            return Optional.of(response);

        } catch (IllegalArgumentException | ConstraintViolationException e) {
            String msg = String.format("create user failed, tenant: '%s', request: '%s', cause: %s",
                                       tenantUrn,
                                       createUserRequest.toString(),
                                       e.getMessage());
            log.error(msg);
            log.debug(msg, e);
            throw e;
        }
    }

    /**
     *
     * @param tenantUrn
     * @param updateUserRequest
     * @return Optional<CreateOrUpdateUserResponse>
     * @throws ConstraintViolationException
     */
    @Override
    public Optional<CreateOrUpdateUserResponse> updateUser(String tenantUrn, UpdateUserRequest updateUserRequest)
        throws ConstraintViolationException {

        try {
            UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);
            Optional<UserEntity> userEntityOptional = userRepository.findByTenantIdAndId(tenantId, UuidUtil.getUuidFromUrn(updateUserRequest.getUrn()));

            if (userEntityOptional.isPresent()) {
                UserEntity userEntity = MergeUtil.merge(userEntityOptional.get(), updateUserRequest);
                userEntity = userRepository.persist(userEntity);
                return Optional.ofNullable(conversionService.convert(userEntity, CreateOrUpdateUserResponse.class));
            }

        } catch (IllegalArgumentException | ConstraintViolationException e) {
            String msg = String.format("update failed, tenant: '%s', request: '%s', cause: %s", tenantUrn, updateUserRequest.toString(), e.toString
                ());
            log.error(msg);
            log.debug(msg, e);
            throw e;
        }

        return Optional.empty();
    }

    /**
     * @param userUrn
     * @return Optional<GetOrDeleteUserResponse>
     */
    @Override
    public Optional<GetOrDeleteUserResponse> findUserByUrn(String tenantUrn, String userUrn) {

        if (userUrn == null || userUrn.isEmpty()) {
            return Optional.empty();
        }

        try {
            UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);
            UUID id = UuidUtil.getUuidFromUrn(userUrn);
            Optional<UserEntity> entity = userRepository.findByTenantIdAndId(tenantId, id);
            if (entity.isPresent()) {
                final GetOrDeleteUserResponse response = conversionService.convert(entity.get(), GetOrDeleteUserResponse.class);
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

    /**
     *
     * @param tenantUrn
     * @param username
     * @return Optional<GetOrDeleteUserResponse>
     */
    @Override
    public Optional<GetOrDeleteUserResponse> findUserByName(String tenantUrn, String username) {

        Optional<UserEntity> entity = userRepository.findByUsernameIgnoreCase(username);
        if (entity.isPresent()) {
            return Optional.of(conversionService.convert(entity.get(), GetOrDeleteUserResponse.class));
        }
        return Optional.empty();
    }

    /**
     *
     * @param tenantUrn
     * @param urn
     * @return Optional<GetOrDeleteUserResponse>
     */
    @Override
    public Optional<GetOrDeleteUserResponse> deleteUserByUrn(String tenantUrn, String urn) {

        UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);
        UUID userId = UuidUtil.getUuidFromUrn(urn);
        Optional<UserEntity> entity = userRepository.findByTenantIdAndId(tenantId, userId);
        if (entity.isPresent()) {
            userRepository.delete(entity.get());
            return Optional.of(conversionService.convert(entity.get(), GetOrDeleteUserResponse.class));
        }
        return Optional.empty();
    }

    /**
     *
     * @param username
     * @param password
     * @return Optional<GetAuthoritiesResponse>
     */
    @Override
    public Optional<GetAuthoritiesResponse> getAuthorities(String username, String password) {

        Optional<UserEntity> userOptional = userRepository.getUserByCredentials(username, password);
        if (!userOptional.isPresent()) {
            return Optional.empty();
        }

        UserEntity user = userOptional.get();
        Optional<Set<AuthorityEntity>> authorityOptional = userRepository.getAuthorities(user.getTenantId(), user.getId());
        Set<AuthorityEntity> authorityEntities = authorityOptional.isPresent() ? authorityOptional.get() : new LinkedHashSet<>();
        Set<String> authorities = authorityEntities.parallelStream()
            .map(AuthorityEntity::getAuthority)
            .collect(toSet());

        GetAuthoritiesResponse response = GetAuthoritiesResponse.builder()
            .urn(UuidUtil.getUserUrnFromUuid(user.getId()))
            .tenantUrn(UuidUtil.getTenantUrnFromUuid(user.getTenantId()))
            .username(user.getUsername())
            .authorities(authorities)
            .build();

        return Optional.of(response);
    }

    // endregion

    // region UTILITY METHODS

    private RoleEntity createAdminRole(String tenantUrn) {
        List<String> authorities = new ArrayList<>();
        authorities.add("smartcosmos.things.read");
        authorities.add("smartcosmos.things.write");

        return createRole(tenantUrn, "Admin", authorities);
    }

    private RoleEntity createUserRole(String tenantUrn) {
        List<String> authorities = new ArrayList<>();
        authorities.add("smartcosmos.things.read");

        return createRole(tenantUrn, "User", authorities);
    }

    private RoleEntity createRole(String tenantUrn, String name, List<String> authorities) {

        CreateOrUpdateRoleRequest createRoleRequest = CreateOrUpdateRoleRequest.builder()
            .name(name)
            .authorities(authorities)
            .active(true)
            .build();

        Optional<CreateOrUpdateRoleResponse> optionalRole = rolePersistenceService.createRole(tenantUrn, createRoleRequest);
        Optional<RoleEntity> savedEntity = rolePersistenceService.findByUrnAsEntity(optionalRole.get().getUrn());
        if (savedEntity.isPresent()) {
            return savedEntity.get();
        }
        throw new IllegalArgumentException();
    }

    // endregion
}

