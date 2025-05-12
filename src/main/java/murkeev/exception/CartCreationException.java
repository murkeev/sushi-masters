package murkeev.exception;

public class CartCreationException extends RuntimeException {
    public CartCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
