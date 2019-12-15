package au.com.eventsecretary;

/**
 * @author sladew
 */
public class ResourceExistsException extends RuntimeException {
    public ResourceExistsException(String message) {
        super(message);
    }
}
