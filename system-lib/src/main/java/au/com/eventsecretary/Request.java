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

    static boolean isSandbox() {
        return MDC.get(SANDBOX_KEY) != null;
    }

    static String bearer() {
        return MDC.get(BEARER_KEY);
    }
}
