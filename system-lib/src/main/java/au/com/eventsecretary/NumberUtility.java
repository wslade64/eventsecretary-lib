package au.com.eventsecretary;

import java.math.BigDecimal;

public interface NumberUtility {
    BigDecimal B100 = BigDecimal.valueOf(100);

    static boolean isNegative(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }
    static boolean isPositive(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }
    static boolean isZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }
}
