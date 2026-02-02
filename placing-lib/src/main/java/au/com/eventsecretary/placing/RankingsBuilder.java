package au.com.eventsecretary.placing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RankingsBuilder<T> {
    private List<Rankings<T>> rankingsList = new ArrayList<>();

    public static <T> RankingsBuilder<T> rankings() {
        return new RankingsBuilder<>();
    }

    public RankingsBuilder<T> rank(Function<T, BigDecimal> rank) {
        return this.rank(rank, null);
    }
    public RankingsBuilder<T> rank(Function<T, BigDecimal> rank, String name) {
        rankingsList.add(Rankings.of(rank, name));
        return this;
    }

    public List<Rankings<T>> list() {
        return rankingsList;
    }
}
