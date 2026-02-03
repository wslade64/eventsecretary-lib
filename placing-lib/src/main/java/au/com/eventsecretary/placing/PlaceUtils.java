package au.com.eventsecretary.placing;

import au.com.eventsecretary.equestrian.scoring.Placing;

import java.math.BigDecimal;
import java.util.EnumSet;

public interface PlaceUtils {
    String NA = "-";
    BigDecimal DNQ = new BigDecimal("-1");
    BigDecimal DNS = new BigDecimal("-2");
    BigDecimal DNF = new BigDecimal("-3");

    BigDecimal EliminatedScore = new BigDecimal("199999999999");

    static String stringValue(BigDecimal value) {
        if (value == null) {
            return NA;
        }
        if (isDNQ(value)) {
            return "DNQ";
        }
        if (isDNS(value)) {
            return "DNS";
        }
        if (isDNF(value)) {
            return "DNF";
        }
        return null;
    }

    static boolean isDNQ(BigDecimal value) {
        if (value == null) {
            return false;
        }
        return value.compareTo(DNQ) == 0;
    }

    static boolean isDNS(BigDecimal value) {
        if (value == null) {
            return false;
        }
        return value.compareTo(DNS) == 0;
    }

    static boolean isDNF(BigDecimal value) {
        if (value == null) {
            return false;
        }
        return value.compareTo(DNF) == 0;
    }

    static boolean isEliminated(BigDecimal value) {
        if (value == null) {
            return false;
        }
        return value.compareTo(EliminatedScore) == 0;
    }

    enum PlaceFormatEnum {
        countback,
        equals
    }

    interface PlaceFormatOptions {
        EnumSet<PlaceFormatEnum> NONE = EnumSet.noneOf(PlaceFormatEnum.class);
        EnumSet<PlaceFormatEnum> COUNTBACK = EnumSet.of(PlaceFormatEnum.countback);
        EnumSet<PlaceFormatEnum> EQUALS = EnumSet.of(PlaceFormatEnum.equals);
        EnumSet<PlaceFormatEnum> ALL = EnumSet.allOf(PlaceFormatEnum.class);
    }

    static boolean isValid(BigDecimal value) {
        if (value == null) {
            return false;
        }
        return value.compareTo(BigDecimal.ZERO) >= 0;
    }

    static boolean hasPlace(Placing placing) {
        return placing != null && placing.getPlace() > 0;
    }

    static String format(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return "";
        }
        if (isEliminated(bigDecimal)) {
            return "E";
        }
        return bigDecimal.stripTrailingZeros().toPlainString();
    }


    static String formatPoints(BigDecimal points) {
        return !isValid(points) ? stringValue(points) : format(points);
    }

    static String formatPlace(Placing phasePlacing, EnumSet<PlaceFormatEnum> ...options) {
        EnumSet<PlaceFormatEnum> option = options.length == 0 ? PlaceFormatOptions.ALL : options[0];

        if (phasePlacing == null || phasePlacing.getPlace() == 0) {
            return "";
        }
        String placeText = Integer.toString(phasePlacing.getPlace());
        if (option.contains(PlaceFormatEnum.equals) && phasePlacing.getPlaceCount() > 1) {
            placeText += "=";
        }
        if (option.contains(PlaceFormatEnum.countback) && phasePlacing.getEqualityReason() != null) {
            placeText += "*";
        }
        return placeText;
    }
}
