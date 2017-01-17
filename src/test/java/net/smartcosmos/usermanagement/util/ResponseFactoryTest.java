package net.smartcosmos.usermanagement.util;

import java.util.Map;

import org.junit.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

import static net.smartcosmos.usermanagement.util.ResponseFactory.MESSAGE;
import static net.smartcosmos.usermanagement.util.ResponseFactory.conflictResponse;
import static net.smartcosmos.usermanagement.util.ResponseFactory.noContentResponse;
import static net.smartcosmos.usermanagement.util.ResponseFactory.notFoundResponse;

public class ResponseFactoryTest {

    // region noContentResponse()

    @Test
    public void thatNoContentResponseReturnsResponseEntity() {

        ResponseEntity<?> responseEntity = noContentResponse();

        assertNotNull(responseEntity);
    }

    @Test
    public void thatNoContentResponseReturns204NoContent() {

        final HttpStatus expectedHttpStatus = HttpStatus.NO_CONTENT;

        ResponseEntity<?> responseEntity = noContentResponse();

        assertEquals(expectedHttpStatus, responseEntity.getStatusCode());
    }

    @Test
    public void thatNoContentResponseReturnsNoBody() {

        ResponseEntity<?> responseEntity = noContentResponse();

        assertFalse(responseEntity.hasBody());
    }

    // endregion

    // region notFoundResponse()

    @Test
    public void thatNotFoundResponseReturnsResponseEntity() {

        ResponseEntity<?> responseEntity = notFoundResponse();

        assertNotNull(responseEntity);
    }

    @Test
    public void thatNotFoundResponseReturns404NotFound() {

        final HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        ResponseEntity<?> responseEntity = notFoundResponse();

        assertEquals(expectedHttpStatus, responseEntity.getStatusCode());
    }

    @Test
    public void thatNotFoundResponseReturnsNoBody() {

        ResponseEntity<?> responseEntity = notFoundResponse();

        assertFalse(responseEntity.hasBody());
    }

    // endregion

    // region conflictResponse()

    @Test
    public void thatConflictResponseReturnsResponseEntity() {

        String message = "";
        ResponseEntity<?> responseEntity = conflictResponse(message);

        assertNotNull(responseEntity);
    }

    @Test
    public void thatConflictResponseReturns409Conflict() {

        final HttpStatus expectedHttpStatus = HttpStatus.CONFLICT;

        String message = "";
        ResponseEntity<?> responseEntity = conflictResponse(message);

        assertEquals(expectedHttpStatus, responseEntity.getStatusCode());
    }

    @Test
    public void thatConflictResponseReturnsNoBodyWhenMessageEmpty() {

        String message = "";
        ResponseEntity<?> responseEntity = conflictResponse(message);

        assertFalse(responseEntity.hasBody());
    }

    @Test
    public void thatConflictResponseReturnsNoBodyWhenMessageNull() {

        String message = null;
        ResponseEntity<?> responseEntity = conflictResponse(message);

        assertFalse(responseEntity.hasBody());
    }

    @Test
    public void thatConflictResponseReturnsBodyWhenMessageSet() {

        String message = "someMessage";
        ResponseEntity<?> responseEntity = conflictResponse(message);

        assertTrue(responseEntity.hasBody());
    }

    @Test
    public void thatConflictResponseBodyContainsMessageField() {

        String expectedMessage = "someMessage";
        ResponseEntity<?> responseEntity = conflictResponse(expectedMessage);

        assertTrue(responseEntity.getBody() instanceof Map);
        Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();

        assertTrue(responseBody.containsKey(MESSAGE));
        assertTrue(responseBody.get(MESSAGE) instanceof String);
        assertEquals(expectedMessage, responseBody.get(MESSAGE));
    }

    // endregion
}
