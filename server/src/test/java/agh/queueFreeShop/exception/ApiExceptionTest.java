package agh.queueFreeShop.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test of ApiExceptionHandler.
 */

public class ApiExceptionTest {
    private ApiException apiException;

    @BeforeEach
    public void setup() {
        Exception e = new Exception("message");
        HttpStatus status = HttpStatus.BAD_REQUEST;

        apiException = new ApiException(
                e.getMessage(),
                status,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
    }

    @Test
    public void should_contain_message() {
        assertThat(apiException.getMessage()).isNotNull();
        assertThat(apiException.getMessage()).isEqualTo("message");
    }

    @Test
    public void should_contain_status() {
        assertThat(apiException.getHttpStatus()).isNotNull();
        assertThat(apiException.getHttpStatus().value()).isEqualTo(400);
    }

    @Test
    public void should_contain_timestamp() {
        assertThat(apiException.getTimestamp()).isNotNull();
        ZonedDateTime y2021 = ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneId.of("Z"));
        assertThat(apiException.getTimestamp().isAfter(y2021)).isTrue();
    }
}
