package au.com.eventsecretary;

/**
 * @author sladew
 */
public class UnexpectedSystemException extends RuntimeException {
    public UnexpectedSystemException(Exception e) {
        super(e.getMessage(), e);
    }
    public UnexpectedSystemException(String message) {
        super(message);
    }
}
