package net.smartcosmos.extension.tenant.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import net.smartcosmos.extension.tenant.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.domain.TenantEntity;
import net.smartcosmos.extension.tenant.domain.UserEntity;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleResponse;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateUserResponse;
import net.smartcosmos.extension.tenant.dto.CreateTenantRequest;
import net.smartcosmos.extension.tenant.dto.CreateTenantResponse;
import net.smartcosmos.extension.tenant.dto.CreateUserRequest;
import net.smartcosmos.extension.tenant.dto.GetTenantResponse;
import net.smartcosmos.extension.tenant.dto.GetOrDeleteUserResponse;
import net.smartcosmos.extension.tenant.dto.TenantEntityAndUserEntityDto;
import net.smartcosmos.extension.tenant.dto.UpdateTenantRequest;
import net.smartcosmos.extension.tenant.dto.UpdateTenantResponse;
import net.smartcosmos.extension.tenant.dto.UpdateUserRequest;
import net.smartcosmos.extension.tenant.repository.TenantRepository;
import net.smartcosmos.extension.tenant.repository.UserRepository;
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
    private final PasswordEncoder passwordEncoder;

    /**
     * @param tenantRepository
     * @param userRepository
     * @param rolePersistenceService
     * @param conversionService
     * @param passwordEncoder
     */
    @Autowired
    public TenantPersistenceService(
        TenantRepository tenantRepository,
        UserRepository userRepository,
        RolePersistenceService rolePersistenceService,
        RoleRepository roleRepository,
        ConversionService conversionService,
        PasswordEncoder passwordEncoder) {

        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.rolePersistenceService = rolePersistenceService;
        this.roleRepository = roleRepository;
        this.conversionService = conversionService;
        this.passwordEncoder = passwordEncoder;
    }

    /******************/
    /* TENANT METHODS */
    /******************/

    /**
     * @param createTenantRequest
     * @return
     * @throws ConstraintViolationException
     */
    @Override
    public Optional<CreateTenantResponse> createTenant(CreateTenantRequest createTenantRequest)
        throws ConstraintViolationException {

        try {
            // This tenant already exists? we're not creating a new one
            if (tenantRepository.findByName(createTenantRequest.getName())
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
                .password(passwordEncoder.encode("PleaseChangeMeImmediately"))
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
     * @return
     * @throws ConstraintViolationException
     */
    @Override
    public Optional<UpdateTenantResponse> updateTenant(UpdateTenantRequest updateTenantRequest)
        throws ConstraintViolationException {

        // This tenant already exists? we're not creating a new one
        Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(UuidUtil.getUuidFromUrn(updateTenantRequest.getUrn()));

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
            String msg = String.format("update failed, tenant: 9+-'%s', cause: %s", updateTenantRequest.getUrn(), e.toString());
            log.error(msg);
            log.debug(msg, e);
            throw e;
        }
        return Optional.empty();
    }

    /**
     * @param tenantUrn
     * @return
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
     * @return
     */
    @Override
    public Optional<GetTenantResponse> findTenantByName(String name) {

        Optional<TenantEntity> entity = tenantRepository.findByName(name);
        if (entity.isPresent()) {
            return Optional.of(conversionService.convert(entity.get(), GetTenantResponse.class));
        }
        return Optional.empty();
    }

    /****************/
    /* USER METHODS */

    /****************/

    @Override
    public Optional<CreateOrUpdateUserResponse> createUser(CreateUserRequest createUserRequest)
        throws ConstraintViolationException {

        try {
            UUID tenantId = UuidUtil.getUuidFromUrn(createUserRequest.getTenantUrn());

            // This user already exists? We're not creating a new one.
            if (userRepository.findByUsernameAndTenantId(
                createUserRequest.getUsername(), tenantId)
                .isPresent()) {

                return Optional.empty();
            }

            UserEntity userEntity = conversionService.convert(createUserRequest, UserEntity.class);
            userEntity.setPassword(passwordEncoder.encode("PleaseChangeMeImmediately"));

            // fetch the roles from the DB since the conversions service converts the role names only
            Set<RoleEntity> roleEntities = userEntity.getRoles()
                .stream()
                .map(roleEntity -> {
                    Optional<RoleEntity> effectiveRole = roleRepository
                            .findByNameAndTenantId(roleEntity.getName(), tenantId);
                    if (effectiveRole.isPresent()) {
                        return effectiveRole.get();
                    }
                    else {
                        String msg = String.format("role '%s' does not exist", roleEntity.getName());
                        throw new IllegalArgumentException(msg);
                    }
                })
                .collect(toSet());

            userEntity.setRoles(roleEntities);

            userEntity = userRepository.save(userEntity);
            return Optional.ofNullable(conversionService.convert(userEntity, CreateOrUpdateUserResponse.class));

        } catch (IllegalArgumentException | ConstraintViolationException e) {
            String msg = String.format("create failed, user: '%s', tenant: '%s', cause: %s",
                    createUserRequest.getUsername(),
                    createUserRequest.getTenantUrn().toString(),
                    e.getMessage());
            log.error(msg);
            log.debug(msg, e);
            throw e;
        }
    }

    /**
     * @param updateUserRequest
     * @return
     * @throws ConstraintViolationException
     */
    @Override
    public Optional<CreateOrUpdateUserResponse> updateUser(UpdateUserRequest updateUserRequest)
        throws ConstraintViolationException {

        Optional<UserEntity> userEntityOptional = userRepository.findById(UuidUtil.getUuidFromUrn(updateUserRequest.getUrn()));

        try {
            if (userEntityOptional.isPresent()) {
                if (updateUserRequest.getActive() != null) {
                    userEntityOptional.get().setActive(updateUserRequest.getActive());
                }
                if (updateUserRequest.getUsername() != null) {
                    userEntityOptional.get().setUsername(updateUserRequest.getUsername());
                }
                if (updateUserRequest.getGivenName() != null) {
                    userEntityOptional.get().setGivenName(updateUserRequest.getGivenName());
                }
                if (updateUserRequest.getSurname() != null) {
                    userEntityOptional.get().setSurname(updateUserRequest.getSurname());
                }
                if (updateUserRequest.getEmailAddress() != null) {
                    userEntityOptional.get().setEmailAddress(updateUserRequest.getEmailAddress());
                }
                if (updateUserRequest.getPassword() != null) {
                    userEntityOptional.get().setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
                }

                UserEntity userEntity = userRepository.save(userEntityOptional.get());
                return Optional.ofNullable(conversionService.convert(userEntity, CreateOrUpdateUserResponse.class));
            }

        } catch (IllegalArgumentException | ConstraintViolationException e) {
            String msg = String.format("update failed, tenant: 9+-'%s', cause: %s", updateUserRequest.getUrn(), e.toString());
            log.error(msg);
            log.debug(msg, e);
            throw e;
        }
        return Optional.empty();
    }

    /**
     * @param userUrn
     * @return
     */
    @Override
    public Optional<GetOrDeleteUserResponse> findUserByUrn(String userUrn) {

        if (userUrn == null || userUrn.isEmpty()) {
            return Optional.empty();
        }

        try {
            UUID id = UuidUtil.getUuidFromUrn(userUrn);
            Optional<UserEntity> entity = userRepository.findById(id);
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
     * @param username
     * @return
     */
    @Override
    public Optional<GetOrDeleteUserResponse> findUserByName(String username) {

        Optional<UserEntity> entity = userRepository.findByUsername(username);
        if (entity.isPresent()) {
            return Optional.of(conversionService.convert(entity.get(), GetOrDeleteUserResponse.class));
        }
        return Optional.empty();
    }

    /**
     * @param urn
     * @return
     */
    @Override
    public Optional<GetOrDeleteUserResponse> deleteUserByUrn(String urn) {

        Optional<UserEntity> entity = userRepository.findById(UuidUtil.getUuidFromUrn(urn));
        if (entity.isPresent()) {
            userRepository.delete(UuidUtil.getUuidFromUrn(urn));
            return Optional.of(conversionService.convert(entity.get(), GetOrDeleteUserResponse.class));
        }
        return Optional.empty();
    }

    /*******************/
    /* UTILITY METHODS */

    /*******************/

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
}

