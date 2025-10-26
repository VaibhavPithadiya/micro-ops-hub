package microopshub.userservice.common.exception;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
