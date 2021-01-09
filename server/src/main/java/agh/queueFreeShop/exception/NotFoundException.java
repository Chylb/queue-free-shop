package agh.queueFreeShop.exception;

/**
 * Used when explicitly throwing 404 exceptions.
 */

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
