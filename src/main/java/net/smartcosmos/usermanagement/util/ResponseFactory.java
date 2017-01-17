package net.smartcosmos.usermanagement.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Utility class to build {@link ResponseEntity} instances the user management service may return.
 */
public class ResponseFactory {

    protected static final String MESSAGE = "message";

    // region Success Responses 2xx

    /**
     * Builds a 204 No Content response without a response body.
     *
     * @return the response entity
     */
    public static ResponseEntity<?> noContentResponse() {

        return ResponseEntity.noContent()
            .build();
    }

    // endregion

    // region Client Error Responses 4xx

    /**
     * Builds a 404 Not Found response without a response body.
     *
     * @return the response entity
     */
    public static ResponseEntity<?> notFoundResponse() {

        return ResponseEntity.notFound()
            .build();
    }

    /**
     * <p>Builds a conflict response that may include an error message:</p>
     * <pre>{ "message": [message] }</pre>
     * <p>If no message is provided, and empty body is returned.</p>
     *
     * @param message the message
     * @return the response entity
     */
    public static ResponseEntity<?> conflictResponse(String message) {

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.CONFLICT);

        if (isBlank(message)) {
            return responseBuilder.build();
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(MESSAGE, message);
        return responseBuilder.contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(body);
    }

    // endregion

}
