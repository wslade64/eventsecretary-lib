package au.com.eventsecretary.placing;

import au.com.eventsecretary.equestrian.scoring.Placing;
import au.com.eventsecretary.equestrian.scoring.PlacingImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static au.com.eventsecretary.CollectionUtility.copyLeft;

/**
 * Places items based on the higher number for the ranking.
 *
 * @author Warwick Slade
 */
public class ListPlacings<T> {
    private final boolean skipEquals;
    private final int limitCountBack;
    private final boolean reversed;

    public ListPlacings(boolean skipEquals, int limitCountBack) {
        this(skipEquals, limitCountBack, true);
    }

    public ListPlacings(boolean skipEquals, int limitCountBack, boolean reversed) {
        this.skipEquals = skipEquals;
        this.limitCountBack = limitCountBack;
        this.reversed = reversed;
    }

    public List<T> calculatePlacings(List<T> items, List<Rankings<T>> rankings, BiConsumer<T, Placing> placingOperator) {
        List<T> applicable = items.stream().filter(item -> rankings.get(0).rank.apply(item) != null).collect(Collectors.toList());
        placeItems(applicable, rankings, 1, placingOperator);
        return items;
    }

    private int placeItems(List<T> items, List<Rankings<T>> rankings, int place, BiConsumer<T, Placing> placingOperator) {
        Function<T, BigDecimal> ranking = rankings.get(0).rank;
        Comparator<T> comparing = Comparator.comparing((T o) -> ranking.apply(o));
        if (reversed) {
            comparing = comparing.reversed();
        }
        items.sort(comparing);

        List<T> equals = new ArrayList<>();
        for (T item : items) {
            if (equals.isEmpty()) {
                equals.add(item);
            } else if (ranking.apply(equals.get(0)).compareTo(ranking.apply(item)) == 0) {
                equals.add(item);
            } else {
                place = assignPlacings(equals, place, rankings, placingOperator);
                equals.clear();
                equals.add(item);
            }
        }
        return assignPlacings(equals, place, rankings, placingOperator);
    }

    private int assignPlacings(List<T> equals, int place, List<Rankings<T>> rankings, BiConsumer<T, Placing> placingOperator) {
        if (equals.size() > 1 && place <= limitCountBack && rankings.size() > 1) {
            return placeItems(equals, copyLeft(rankings), place, placingOperator);
        }
        Placing placing = new PlacingImpl();
        placing.setPlace(place);
        placing.setPlaceCount(equals.size());
        placing.setEqualityReason(rankings.get(0).name);

        equals.forEach(item -> placingOperator.accept(item, placing));

        return place + (skipEquals ? equals.size() : 1);
    }
}
