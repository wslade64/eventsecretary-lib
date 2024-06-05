package au.com.eventsecretary;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public interface Safe {
    public static <T, I, R> R extract(T context, Function<T, I> extractor, Function<I, R> value) {
        if (context == null) {
            return null;
        }
        I target = extractor.apply(context);
        if (target == null) {
            return null;
        }
        return value.apply(target);
    }

    interface Setter<A, B> {
        void apply(A a, B b);
    }
    static <T, I, V> void inject(T context, Function<T, I> extractor, Supplier<I> newFunction, Consumer<I> injector, Setter<I, V> setter, V value) {
        if (value == null ) {
            return;
        }
        I target = extractor.apply(context);
        if (target == null) {
            target = newFunction.get();
            injector.accept(target);
        }
        setter.apply(target, value);
    }
}
