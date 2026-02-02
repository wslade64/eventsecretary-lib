package au.com.eventsecretary.placing;

import java.math.BigDecimal;
import java.util.function.Function;

public final class Rankings<T> {
    public final Function<T, BigDecimal> rank;
    public final String name;

    private Rankings(Function<T, BigDecimal> rank, String name) {
        this.rank = rank;
        this.name = name;
    }

    static <T> Rankings<T> of(Function<T, BigDecimal> rank, String name) {
        return new Rankings<>(rank, name);
    }
}
