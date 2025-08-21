package map.creator.map.utils.exception;

// when the program behaves not as planned
public class UnexpectedBehaviorException extends RuntimeException {
    public UnexpectedBehaviorException(String message) {
        super(message);
    }

    public UnexpectedBehaviorException() {
        super();
    }
}
