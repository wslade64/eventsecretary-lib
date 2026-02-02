package au.com.eventsecretary;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public interface CollectionUtility {
    static <T> T first(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

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
        Map<String, String> nvp = new LinkedHashMap<>();
        for (String s : list.split(",")) {
            String[] split = s.split("=");
            nvp.put(split[0], split[1]);
        }
        return nvp;
    }

    static String toString(Boolean value) {
        return value == null ? "" : (value.booleanValue() ? "Yes" : "No");
    }

    static <T> Map<String, List<T>> group(List<T> bigList, Function<T, String> keyProvider) {
        Map<String, List<T>> sections = new HashMap<>();
        for (T target : bigList) {
            String key = keyProvider.apply(target);
            List<T> list = sections.get(key);
            if (list == null) {
                sections.put(key, list = new ArrayList<>());
            }
            list.add(target);
        }
        return sections;
    }

    static String join(String sep, Object... objects) {
        if (objects == null) {
            throw new IllegalArgumentException("Object varargs must not be null");
        } else {
            StringBuilder result = new StringBuilder();

            boolean first = true;
            for (Object object : objects) {
                if (object == null) {
                    continue;
                }
                String s = object.toString();
                if (s.isEmpty()) {
                    continue;
                }

                if (!first) {
                    result.append(sep);
                } else {
                    first = false;
                }
                result.append(s);
            }
            return result.toString();
        }
    }

    static <T> boolean listEquals(List<T> left, List<T> right) {
        if (left.size() != right.size()) {
            return false;
        }
        for (int i = 0; i < left.size(); i++) {
            if (!right.contains(left.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static <T> List<T> copyLeft(List<T> equalityRules) {
        List<T> leftRules = new ArrayList<>();
        for (int i = 1; i < equalityRules.size(); i++) {
            leftRules.add(equalityRules.get(i));
        }
        return leftRules;
    }


}
