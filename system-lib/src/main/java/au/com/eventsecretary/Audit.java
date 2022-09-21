package au.com.eventsecretary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sladew
 */
public class Audit {
    private static final Logger logger = LoggerFactory.getLogger("audit");
    private static final Logger security = LoggerFactory.getLogger("security");

    private static final String[] param = {
             "{}",
             "{},{}",
             "{},{},{}",
             "{},{},{},{}",
             "{},{},{},{},{}",
             "{},{},{},{},{},{}",
             "{},{},{},{},{},{},{}",
             "{},{},{},{},{},{},{},{}"
    };

    private Audit() {
    }

    public static void audit(Object... values) {
        logger.info(param[values.length-1], values);
    }
    public static void security(Object... values) {
        security.info(param[values.length-1], values);
    }
}
