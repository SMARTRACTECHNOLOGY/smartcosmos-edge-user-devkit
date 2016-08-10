package net.smartcosmos.extension.tenant.domain;

import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.*;

import net.smartcosmos.extension.tenant.TenantPersistenceTestApplication;

@org.springframework.boot.test.SpringApplicationConfiguration(classes = { TenantPersistenceTestApplication.class })
public class TenantEntityTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void thatEverythingIsOk() {

        TenantEntity tenantEntity = TenantEntity.builder().name("some name").build();

        Set<ConstraintViolation<TenantEntity>> violationSet = validator.validate(tenantEntity);

        assertTrue(violationSet.isEmpty());
    }
}
