package net.smartcosmos.extension.tenant.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

/**
 * Data Transfer Object for REST code/message responses.
 */
@JsonIgnoreProperties({"version"})
@Data
public class MessageDto {

    private static final int VERSION = 1;
    @Setter(AccessLevel.NONE)
    private int version = VERSION;

    private final int code;
    private final String message;

    @Builder
    protected MessageDto(int code, String message) {
        this.code = code;
        this.message = message;

        this.version = VERSION;
    }
}
