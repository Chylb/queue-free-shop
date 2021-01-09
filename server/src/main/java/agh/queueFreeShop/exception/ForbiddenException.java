package agh.queueFreeShop.exception;

/**
 * Used when explicitly throwing 403 exceptions.
 */

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}