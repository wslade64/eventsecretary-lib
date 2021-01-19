package au.com.eventsecretary.simm;

import au.com.eventsecretary.common.Identifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class IdentifiableCache<T extends Identifiable> {
    private final Function<String, T> findById;
    private final Function<String, T> findByCode;

    private List<T> cache = new ArrayList<>();

    public IdentifiableCache(Function<String, T> findById
            , Function<String, T> findByCode) {
        this.findById = findById;
        this.findByCode = findByCode;
    }

    public T findById(String id) {
        T target = IdentifiableUtils.findById(cache, id);
        if (target != null) {
            return target;
        }
        target = findById.apply(id);
        if (target != null) {
            cache.add(target);
        }
        return target;
    }

    public T findByCode(String code) {
        T target = IdentifiableUtils.findByCode(cache, code);
        if (target != null) {
            return target;
        }
        target = findByCode.apply(code);
        if (target != null) {
            cache.add(target);
        }
        return target;
    }
}
