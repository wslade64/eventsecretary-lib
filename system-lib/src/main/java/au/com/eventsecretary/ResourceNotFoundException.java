package au.com.eventsecretary;

/**
 * @author sladew
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
