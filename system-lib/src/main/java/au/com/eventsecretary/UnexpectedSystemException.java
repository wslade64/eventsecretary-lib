package au.com.eventsecretary;

/**
 * @author sladew
 */
public class UnexpectedSystemException extends RuntimeException {
    public UnexpectedSystemException(Exception e) {
    }
    public UnexpectedSystemException(String message) {
        super(message);
    }
}
