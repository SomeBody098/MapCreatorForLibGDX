package map.creator.map.utils.exception;

// when something not initialized
public class NotInitializedObjectException extends RuntimeException {
    public NotInitializedObjectException(String message) {
        super(message);
    }

    public NotInitializedObjectException() {
        super("Some object not initialized!");
    }
}
