package net.smartcosmos.dao.tenant.domain;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.*;

import net.smartcosmos.ext.tenant.domain.UserEntity;
import net.smartcosmos.ext.tenant.util.UuidUtil;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
public class UserEntityTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void thatEverythingIsOk() {

        UserEntity userEntity = UserEntity.builder()
            .tenantId(UuidUtil.getNewUuid())
            .username("some name")
            .emailAddress("timc@example.com")
            .givenName("Tim")
            .surname("Cross")
            .build();

        Set<ConstraintViolation<UserEntity>> violationSet = validator.validate(userEntity);

        assertTrue(violationSet.isEmpty());
    }
}
