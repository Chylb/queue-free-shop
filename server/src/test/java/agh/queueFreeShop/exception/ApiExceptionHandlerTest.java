package agh.queueFreeShop.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test of ApiExceptionHandler.
 */

public class ApiExceptionHandlerTest {
    private ApiExceptionHandler apiExceptionHandler;

    @BeforeEach
    public void setup() {
        apiExceptionHandler = new ApiExceptionHandler();
    }

    @Test
    public void responseEntity_should_not_be_null() {
        ForbiddenException exception = new ForbiddenException("message");
        ResponseEntity<Object> responseEntity = apiExceptionHandler.handleForbiddenException(exception, null);
        assertThat(responseEntity).isNotNull();
    }

    @Test
    public void responseEntity_should_contain_message() {
        ForbiddenException exception = new ForbiddenException("message");
        ResponseEntity<Object> responseEntity = apiExceptionHandler.handleForbiddenException(exception, null);

        ApiException apiException = (ApiException) responseEntity.getBody();
        assertThat(apiException).isNotNull();
        assertThat(apiException.getMessage()).isEqualTo("message");
    }

    @Test
    public void responseEntity_should_contain_timestamp() {
        ForbiddenException exception = new ForbiddenException("message");
        ResponseEntity<Object> responseEntity = apiExceptionHandler.handleForbiddenException(exception, null);

        ApiException apiException = (ApiException) responseEntity.getBody();
        assertThat(apiException).isNotNull();

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Z"));
        boolean before = apiException.getTimestamp().isBefore(now);
        boolean equal = apiException.getTimestamp().isEqual(now);

        assertThat(before || equal).isTrue();
    }

    @Test
    public void responseEntity_status_should_be_403_for_forbiddenException() {
        ForbiddenException exception = new ForbiddenException("message");
        ResponseEntity<Object> responseEntity = apiExceptionHandler.handleForbiddenException(exception, null);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void responseEntity_status_should_be_404_for_notFoundException() {
        NotFoundException exception = new NotFoundException("message");
        ResponseEntity<Object> responseEntity = apiExceptionHandler.handleNotFoundException(exception, null);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void responseEntity_status_should_be_422_for_unprocessableEntityException() {
        UnprocessableEntityException exception = new UnprocessableEntityException("message");
        ResponseEntity<Object> responseEntity = apiExceptionHandler.handleUnprocessableEntityException(exception, null);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(422);
    }
}
