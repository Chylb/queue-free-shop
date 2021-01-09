package agh.queueFreeShop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException e, WebRequest webRequest) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        return createResponseEntity(e, status);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e, WebRequest webRequest) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        return createResponseEntity(e, status);
    }

    @ExceptionHandler(value = {UnprocessableEntityException.class})
    public ResponseEntity<Object> handleUnprocessableEntityException(UnprocessableEntityException e, WebRequest webRequest) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        return createResponseEntity(e, status);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return createResponseEntity(e, status);
    }

    private static ResponseEntity<Object> createResponseEntity(Exception e, HttpStatus status){
        ApiException apiException = new ApiException(
                e.getMessage(),
                status,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, status);
    }
}
