package net.smartcosmos.extension.tenant.impl;

import lombok.extern.slf4j.Slf4j;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.domain.TenantEntity;
import net.smartcosmos.extension.tenant.domain.UserEntity;
import net.smartcosmos.extension.tenant.dto.TenantEntityAndUserEntityDto;
import net.smartcosmos.extension.tenant.dto.authentication.GetAuthoritiesResponse;
import net.smartcosmos.extension.tenant.dto.role.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.role.RoleResponse;
import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantRequest;
import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantResponse;
import net.smartcosmos.extension.tenant.dto.tenant.TenantResponse;
import net.smartcosmos.extension.tenant.dto.tenant.UpdateTenantRequest;
import net.smartcosmos.extension.tenant.dto.user.CreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.dto.user.CreateUserResponse;
import net.smartcosmos.extension.tenant.dto.user.UserResponse;
import net.smartcosmos.extension.tenant.repository.RoleRepository;
import net.smartcosmos.extension.tenant.repository.TenantRepository;
import net.smartcosmos.extension.tenant.repository.UserRepository;
import net.smartcosmos.extension.tenant.util.MergeUtil;
import net.smartcosmos.extension.tenant.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.*;

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

        // Usernames have to be unique in the system for now, so we need that:
        if (userAlreadyExists(createTenantRequest.getUsername())) {
            return Optional.empty();
        }

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
     * @return Optional<TenantResponse>
     * @throws ConstraintViolationException
     */
    @Override
    public Optional<TenantResponse> updateTenant(String tenantUrn, UpdateTenantRequest updateTenantRequest)
        throws ConstraintViolationException {

        Optional<TenantEntity> tenantEntityOptional = tenantRepository.findById(UuidUtil.getUuidFromUrn(tenantUrn));

        if (tenantEntityOptional.isPresent()) {
            try {
                if (updateTenantRequest.getActive() != null) {
                    tenantEntityOptional.get()
                        .setActive(updateTenantRequest.getActive());
                }
                if (updateTenantRequest.getName() != null) {
                    tenantEntityOptional.get()
                        .setName(updateTenantRequest.getName());
                }
                TenantEntity tenantEntity = tenantRepository.save(tenantEntityOptional.get());
                return Optional.ofNullable(conversionService.convert(tenantEntity, TenantResponse.class));
            } catch(IllegalArgumentException | ConstraintViolationException e){
                String msg = String.format("update failed, tenant: '%s', request: '%s', cause: %s", tenantUrn, updateTenantRequest.toString(), e
                    .toString());
                log.error(msg);
                log.debug(msg, e);
                throw e;
            }
        }

        return Optional.empty();
    }

    /**
     * @param tenantUrn
     * @return Optional<GetTenantResponse>
     */
    @Override
    public Optional<TenantResponse> findTenantByUrn(String tenantUrn) {

        if (tenantUrn == null || tenantUrn.isEmpty()) {
            return Optional.empty();
        }

        try {
            UUID id = UuidUtil.getUuidFromUrn(tenantUrn);
            Optional<TenantEntity> entity = tenantRepository.findById(id);
            if (entity.isPresent()) {
                final TenantResponse response = conversionService.convert(entity.get(), TenantResponse.class);
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
    public Optional<TenantResponse> findTenantByName(String name) {

        Optional<TenantEntity> entity = tenantRepository.findByNameIgnoreCase(name);
        if (entity.isPresent()) {
            return Optional.of(conversionService.convert(entity.get(), TenantResponse.class));
        }
        return Optional.empty();
    }

    @Override
    public List<TenantResponse> findAllTenants() {

        List<TenantEntity> entityList = tenantRepository.findAll();
        return convertList(entityList,TenantEntity.class, TenantResponse.class);
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
     * @return Optional<CreateUserResponse>
     * @throws ConstraintViolationException
     */
    @Override
    public Optional<CreateUserResponse> createUser(String tenantUrn, CreateOrUpdateUserRequest createUserRequest)
        throws ConstraintViolationException {

        if (userAlreadyExists(createUserRequest.getUsername())) {
            // This user already exists? We're not creating a new one.
            return Optional.empty();
        }

        String password = INITIAL_PASSWORD;

        try {
            UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);

            UserEntity userEntity = conversionService.convert(createUserRequest, UserEntity.class);
            userEntity.setId(UuidUtil.getNewUuid());
            userEntity.setTenantId(tenantId);
            userEntity.setPassword(password);
            userEntity = userRepository.persist(userEntity);
            userEntity = userRepository.addRolesToUser(userEntity.getTenantId(), userEntity.getId(), createUserRequest.getRoles()).get();

            CreateUserResponse response = conversionService.convert(userEntity, CreateUserResponse.class);
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

    @Override
    public Optional<UserResponse> updateUser(String tenantUrn, String userUrn, CreateOrUpdateUserRequest updateUserRequest)
        throws ConstraintViolationException {

        try {
            UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);
            Optional<UserEntity> userEntityOptional = userRepository.findByTenantIdAndId(tenantId, UuidUtil.getUuidFromUrn(userUrn));

            if (userEntityOptional.isPresent()) {
                UserEntity userEntity = MergeUtil.merge(userEntityOptional.get(), updateUserRequest);
                userEntity = userRepository.persist(userEntity);
                return Optional.ofNullable(conversionService.convert(userEntity, UserResponse.class));
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
     * @return Optional<UserResponse>
     */
    @Override
    public Optional<UserResponse> findUserByUrn(String tenantUrn, String userUrn) {

        if (userUrn == null || userUrn.isEmpty()) {
            return Optional.empty();
        }

        try {
            UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);
            UUID id = UuidUtil.getUuidFromUrn(userUrn);
            Optional<UserEntity> entity = userRepository.findByTenantIdAndId(tenantId, id);
            if (entity.isPresent()) {
                final UserResponse response = conversionService.convert(entity.get(), UserResponse.class);
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
     * @return Optional<UserResponse>
     */
    @Override
    public Optional<UserResponse> findUserByName(String tenantUrn, String username) {

        Optional<UserEntity> entity = userRepository.findByUsernameIgnoreCase(username);
        if (entity.isPresent()) {
            return Optional.of(conversionService.convert(entity.get(), UserResponse.class));
        }
        return Optional.empty();
    }

    @Override
    public List<UserResponse> findAllUsers(String tenantUrn) {

        UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);
        List<UserEntity> entityList = userRepository.findByTenantId(tenantId);
        return convertList(entityList,UserEntity.class, UserResponse.class);
    }

    /**
     *
     * @param tenantUrn
     * @param urn
     * @return Optional<UserResponse>
     */
    @Override
    public Optional<UserResponse> deleteUserByUrn(String tenantUrn, String urn) {

        UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);
        UUID userId = UuidUtil.getUuidFromUrn(urn);
        Optional<UserEntity> entity = userRepository.findByTenantIdAndId(tenantId, userId);
        if (entity.isPresent()) {
            userRepository.delete(entity.get());
            return Optional.of(conversionService.convert(entity.get(), UserResponse.class));
        }
        return Optional.empty();
    }

    // endregion

    // region UTILITY METHODS

    private RoleEntity createAdminRole(String tenantUrn) {
        List<String> authorities = new ArrayList<>();
        authorities.add("https://authorities.smartcosmos.net/things/read");
        authorities.add("https://authorities.smartcosmos.net/things/write");

        return createRole(tenantUrn, "Admin", authorities);
    }

    private RoleEntity createUserRole(String tenantUrn) {
        List<String> authorities = new ArrayList<>();
        authorities.add("https://authorities.smartcosmos.net/things/read");

        return createRole(tenantUrn, "User", authorities);
    }

    private RoleEntity createRole(String tenantUrn, String name, List<String> authorities) {

        CreateOrUpdateRoleRequest createRoleRequest = CreateOrUpdateRoleRequest.builder()
            .name(name)
            .authorities(authorities)
            .active(true)
            .build();

        Optional<RoleResponse> optionalRole = rolePersistenceService.createRole(tenantUrn, createRoleRequest);
        Optional<RoleEntity> savedEntity = rolePersistenceService.findByUrnAsEntity(tenantUrn, optionalRole.get().getUrn());
        if (savedEntity.isPresent()) {
            return savedEntity.get();
        }
        throw new IllegalArgumentException();
    }

    /**
     * Uses the conversion service to convert a typed list into another typed list.
     *
     * @param list the list
     * @param sourceClass the class of the source type
     * @param targetClass the class of the target type
     * @param <S> the generic source type
     * @param <T> the generic target type
     * @return the converted typed list
     */
    @SuppressWarnings("unchecked")
    private <S, T> List<T> convertList(List<S> list, Class sourceClass, Class targetClass) {

        TypeDescriptor sourceDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(sourceClass));
        TypeDescriptor targetDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(targetClass));

        return (List<T>) conversionService.convert(list, sourceDescriptor, targetDescriptor);
    }

    // endregion
}

