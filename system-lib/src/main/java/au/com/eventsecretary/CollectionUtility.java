package au.com.eventsecretary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public interface CollectionUtility {
    static <T> T last(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    static <T> T getSafe(Supplier<T> get, Consumer<T> set, Supplier<T> supplier) {
        T t = get.get();
        if (t == null) {
            t = supplier.get();
            set.accept(t);
        }
        return t;
    }

    static <T> void addArrayToList(T target, Function<T, List<String>> function, String[] items)  {
        if (items == null || items.length == 0) {
            return;
        }
        List<String> apply = function.apply(target);
        apply.addAll(Arrays.asList(items));
    }

    static List<Integer> toIntegerList(String list) {
        List<Integer> ilist = new ArrayList<>();
        if (list == null || list.length() == 0) {
            return ilist;
        }
        String[] split = list.split(",");
        for (String s : split) {
            ilist.add(Integer.parseInt(s));
        }

        return ilist;
    }

    static Collection<? extends String> toStringList(String list) {
        String[] split = list.split(",");
        return Arrays.asList(split);
    }

    static BigDecimal[] toBigDecimalArray(int[] values) {
        BigDecimal[] bigDecimals = new BigDecimal[values.length];
        for (int i = 0; i < values.length; i++) {
            bigDecimals[i] = new BigDecimal(values[i]);
        }
        return bigDecimals;
    }

    static Map<String, String> nameValuePairs(String list) {
        Map<String, String> nvp = new HashMap<>();
        for (String s : list.split(",")) {
            String[] split = s.split("=");
            nvp.put(split[0], split[1]);
        }
        return nvp;
    }

    static String toString(Boolean value) {
        return value == null ? "" : (value.booleanValue() ? "Yes" : "No");
    }
}
