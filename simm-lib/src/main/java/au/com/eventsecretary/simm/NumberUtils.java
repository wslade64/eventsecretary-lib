package au.com.eventsecretary.simm;

import java.math.BigDecimal;

import static au.com.eventsecretary.NumberUtility.B100;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class NumberUtils {
    public static boolean isWholeNumber(BigDecimal number) {
        return number.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0;
    }

    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        if (isWholeNumber(amount)) {
            return String.format("$%,d", amount.intValue());
        }
        return String.format("$%,d.%02d", amount.intValue(), amount.remainder(BigDecimal.ONE).multiply(B100).intValue());
    }

}
