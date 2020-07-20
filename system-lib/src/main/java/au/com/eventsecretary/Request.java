package au.com.eventsecretary;

import org.slf4j.MDC;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public interface Request {
    String SANDBOX = "sandbox";
    String IDENTITY_KEY = "Identity";
    String BEARER_KEY = "Bearer";
    String SANDBOX_KEY = "Sandbox";
    String PERSON_KEY = "Person";
    String AUTH_COOKIE = "es-auth";

    static boolean isSandbox() {
        return MDC.get(SANDBOX_KEY) != null;
    }

    static String bearer() {
        return MDC.get(BEARER_KEY);
    }

    static String person() {
        return MDC.get(PERSON_KEY);
    }

    static String identity() {
        return MDC.get(IDENTITY_KEY);
    }
}
