package net.smartcosmos.extension.tenant.domain;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.*;

import net.smartcosmos.usermanagement.DevKitUserManagementService;
import net.smartcosmos.usermanagement.tenant.domain.TenantEntity;

import static org.junit.Assert.*;

@org.springframework.boot.test.SpringApplicationConfiguration(classes = { DevKitUserManagementService.class })
public class TenantEntityTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void thatEverythingIsOk() {

        TenantEntity tenantEntity = TenantEntity.builder()
            .name("some name")
            .build();

        Set<ConstraintViolation<TenantEntity>> violationSet = validator.validate(tenantEntity);

        assertTrue(violationSet.isEmpty());
    }
}
