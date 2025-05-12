package murkeev.exception;

public class CartItemCreationException extends RuntimeException {
    public CartItemCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}

