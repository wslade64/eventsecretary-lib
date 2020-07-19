package au.com.eventsecretary;

/**
 * @author sladew
 */
public class ValidationException extends RuntimeException {
    private String code;

    public ValidationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
