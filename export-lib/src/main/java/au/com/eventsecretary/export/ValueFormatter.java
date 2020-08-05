package au.com.eventsecretary.export;

public interface ValueFormatter<S, T> {
    T format(S value);
}
