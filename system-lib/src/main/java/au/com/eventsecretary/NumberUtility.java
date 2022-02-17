package au.com.eventsecretary;

import java.math.BigDecimal;

public interface NumberUtility {
    BigDecimal B100 = BigDecimal.valueOf(100);
    BigDecimal B1000 = BigDecimal.valueOf(1000);

    static boolean isNegative(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }
    static boolean isPositive(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }
    static boolean isZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    static BigDecimal add(BigDecimal left, BigDecimal right) {
        if (left == null && right == null) {
            return null;
        }
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        return left.add(right);
    }

    static BigDecimal add(BigDecimal ...more) {
        if (more.length == 0) {
            return null;
        }
        BigDecimal sum = BigDecimal.ZERO;
        boolean atLeastOne = false;
        for (int i = 0; i < more.length; i++) {
            if (more[i] != null) {
                sum = sum.add(more[i]);
                atLeastOne = true;
            }
        }
        return atLeastOne ? sum : null;
    }

}
