package au.com.eventsecretary.client;

/**
 * @author sladew
 */
public class UnauthorizedException extends RuntimeException {
    private boolean noCredentials;

    public UnauthorizedException() {
    }

    public UnauthorizedException(boolean noCredentials) {
        this.noCredentials = noCredentials;
    }

    public boolean noCredentitals() {
        return noCredentials;
    }
}
