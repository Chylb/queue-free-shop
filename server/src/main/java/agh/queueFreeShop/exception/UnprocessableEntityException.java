package agh.queueFreeShop.exception;

/**
 * Used when explicitly throwing 422 exceptions.
 */

public class UnprocessableEntityException extends RuntimeException {
    public UnprocessableEntityException(String message) {
        super(message);
    }
}
