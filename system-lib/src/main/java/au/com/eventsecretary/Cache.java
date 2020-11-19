package au.com.eventsecretary;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Cache<T> {
    Map<String, T> orderCache = new HashMap<>();
    Function<String, T> function;

    public Cache(Function<String, T> function) {
        this.function = function;
    }

    public T get(String key) {
        if (key == null) {
            return null;
        }
        T t = orderCache.get(key);
        if (t != null) {
            return t;
        }
        t = this.function.apply(key);
        if (t != null) {
            orderCache.put(key, t);
        }
        return t;
    }
}
