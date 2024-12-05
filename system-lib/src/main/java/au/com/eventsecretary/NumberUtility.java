package au.com.eventsecretary;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface NumberUtility {
    BigDecimal B100 = BigDecimal.valueOf(100);
    BigDecimal B1000 = BigDecimal.valueOf(1000);
    BigDecimal BLARGE = BigDecimal.valueOf(1000000);

    static boolean isPositive(Integer number) {
        return number != null && number > 0;
    }
    static boolean isZero(Integer number) {
        return number == null || number == 0;
    }
    static int value(Integer number) {
        return number == null ? 0 : number;
    }

    static boolean isNegative(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }
    static boolean isPositive(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }
    static boolean isZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    static BigDecimal addPositive(BigDecimal left, BigDecimal right) {
        if (left == null && right == null) {
            return null;
        }
        if (left == null) {
            if (isNegative(right)) {
                return BigDecimal.ZERO;
            }
            return right;
        }
        if (right == null) {
            if (isNegative(left)) {
                return BigDecimal.ZERO;
            }
            return left;
        }
        if (isNegative(left) && isNegative(right)) {
            return BigDecimal.ZERO;
        }
        if (isNegative(left)) {
            return right;
        }
        if (isNegative(right)) {
            return left;
        }
        return left.add(right);
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

    static BigDecimal multiply(BigDecimal left, BigDecimal right) {
        if (left == null || right == null) {
            return null;
        }
        return left.multiply(right);
    }

    static BigDecimal extractNumber(BigDecimal value, int index, int size) {
        BigDecimal bsize = new BigDecimal(size);
        while (index-- != 0) {
            value = value.divide(bsize, 0, RoundingMode.FLOOR);
        }
        return value.remainder(bsize);
    }

    static int extractNumber(int value, int index, int size) {
        while (index-- != 0) {
            value = value / size;
        }
        return value % size;
    }

    static int compareTo(BigDecimal left, BigDecimal right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return -1;
        }
        if (right == null) {
            return 1;
        }
        return left.compareTo(right);
    }

    static String trimDecimal(String decimal) {
        if (decimal == null || decimal.length() == 0) {
            return decimal;
        }
        int index = decimal.indexOf(".");
        if (index == -1) {
            return decimal;
        }
        String left = decimal.substring(0, index);
        String right = decimal.substring(index + 1);

        StringBuffer buffer = new StringBuffer();
        boolean done = false;
        for (int i = right.length() - 1; i >= 0; i--) {
            char c = right.charAt(i);
            if (c != '0' || done) {
                done = true;
                buffer.insert(0, c);
            }
        }
        if (buffer.length() == 0) {
            return left;
        }
        return left + "." + buffer.toString();
    }

    public static int rate(int distance, int factor) {
        return (int)Math.ceil((float)(distance) * 60 / factor);
    }

}
