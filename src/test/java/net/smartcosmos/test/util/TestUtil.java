package net.smartcosmos.test.util;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class containing helper methods used across different test classes.
 */
public class TestUtil {

    /**
     * Converts a body object to a byte array to be used as JSON request body.
     *
     * @param object the request body object
     * @return JSON byte array
     * @throws IOException if conversion to JSON failed
     */
    public static byte[] json(Object object) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

}
