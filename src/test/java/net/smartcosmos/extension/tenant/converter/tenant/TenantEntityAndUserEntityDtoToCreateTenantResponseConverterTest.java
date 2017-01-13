package net.smartcosmos.extension.tenant.converter.tenant;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableSet;

import org.junit.*;

import net.smartcosmos.cluster.userdetails.domain.RoleEntity;
import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.usermanagement.tenant.converter.TenantEntityAndUserEntityDtoToCreateTenantResponseConverter;
import net.smartcosmos.usermanagement.tenant.domain.TenantEntity;
import net.smartcosmos.usermanagement.tenant.dto.CreateTenantResponse;
import net.smartcosmos.usermanagement.tenant.dto.TenantEntityAndUserEntityDto;
import net.smartcosmos.usermanagement.user.dto.CreateUserResponse;

import static org.junit.Assert.*;

import static net.smartcosmos.cluster.userdetails.util.UuidUtil.getTenantUrnFromUuid;
import static net.smartcosmos.cluster.userdetails.util.UuidUtil.getUserUrnFromUuid;

public class TenantEntityAndUserEntityDtoToCreateTenantResponseConverterTest {

    private final TenantEntityAndUserEntityDtoToCreateTenantResponseConverter converter =
        new TenantEntityAndUserEntityDtoToCreateTenantResponseConverter();

    final Boolean EXPECTED_TENANT_ACTIVE = true;
    final UUID TENANT_ID = UUID.randomUUID();
    final String EXPECTED_TENANT_URN = getTenantUrnFromUuid(TENANT_ID);
    final String EXPECTED_TENANT_NAME = "someTenantName";

    final Boolean EXPECTED_USER_ACTIVE = true;
    final String EXPECTED_USER_NAME = "someUsername";
    final UUID USER_ID = UUID.randomUUID();
    final String EXPECTED_USER_URN = getUserUrnFromUuid(USER_ID);
    final String EXPECTED_PASSWORD = "somePassword";
    final String EXPECTED_EMAIL_ADDRESS = "someEmailAddress";
    final String EXPECTED_GIVEN_NAME = "someGivenName";
    final String EXPECTED_SURNAME = "someSurname";
    final String EXPECTED_ROLE_NAME = "Admin";
    final RoleEntity ROLE = RoleEntity.builder()
        .name(EXPECTED_ROLE_NAME)
        .build();
    final Set<RoleEntity> ROLES = ImmutableSet.of(ROLE);

    TenantEntityAndUserEntityDto ENTITY = TenantEntityAndUserEntityDto.builder()
        .tenantEntity(TenantEntity.builder()
                          .active(EXPECTED_TENANT_ACTIVE)
                          .id(TENANT_ID)
                          .name(EXPECTED_TENANT_NAME)
                          .build())
        .userEntity(UserEntity.builder()
                        .active(EXPECTED_USER_ACTIVE)
                        .username(EXPECTED_USER_NAME)
                        .emailAddress(EXPECTED_EMAIL_ADDRESS)
                        .givenName(EXPECTED_GIVEN_NAME)
                        .roles(ROLES)
                        .id(USER_ID)
                        .surname(EXPECTED_SURNAME)
                        .tenantId(TENANT_ID)
                        .build())
        .rawPassword(EXPECTED_PASSWORD)
        .build();

    @Test
    public void thatConvertReturnsCreateTenantResponse() {

        CreateTenantResponse response = converter.convert(ENTITY);

        assertNotNull(response);
    }

    @Test
    public void thatConvertReturnsTenantUrn() {

        CreateTenantResponse response = converter.convert(ENTITY);

        assertEquals(EXPECTED_TENANT_URN, response.getUrn());
    }

    @Test
    public void thatConvertReturnsAdmin() {

        CreateTenantResponse response = converter.convert(ENTITY);

        assertNotNull(response.getAdmin());
    }

    @Test
    public void thatConvertReturnsAdminUsername() {

        CreateTenantResponse response = converter.convert(ENTITY);
        CreateUserResponse adminUser = response.getAdmin();

        assertEquals(EXPECTED_USER_NAME, adminUser.getUsername());
    }

    @Test
    public void thatConvertReturnsAdminUserUrn() {

        CreateTenantResponse response = converter.convert(ENTITY);
        CreateUserResponse adminUser = response.getAdmin();

        assertEquals(EXPECTED_USER_URN, adminUser.getUrn());
    }

    @Test
    public void thatConvertReturnsAdminTenantUrn() {

        CreateTenantResponse response = converter.convert(ENTITY);
        CreateUserResponse adminUser = response.getAdmin();

        assertEquals(EXPECTED_TENANT_URN, adminUser.getTenantUrn());
    }

    @Test
    public void thatConvertReturnsAdminPassword() {

        CreateTenantResponse response = converter.convert(ENTITY);
        CreateUserResponse adminUser = response.getAdmin();

        assertEquals(EXPECTED_PASSWORD, adminUser.getPassword());
    }

    @Test
    public void thatConvertReturnsUserRoles() {

        CreateTenantResponse response = converter.convert(ENTITY);
        CreateUserResponse adminUser = response.getAdmin();

        assertNotNull(adminUser.getRoles());
    }

    @Test
    public void thatConvertReturnsAdminRole() {

        CreateTenantResponse response = converter.convert(ENTITY);
        CreateUserResponse adminUser = response.getAdmin();
        Collection<String> roles = adminUser.getRoles();

        assertFalse(roles.isEmpty());
        assertTrue(roles.contains(EXPECTED_ROLE_NAME));
    }
}
