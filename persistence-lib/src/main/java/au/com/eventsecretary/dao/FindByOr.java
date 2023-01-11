package au.com.eventsecretary.dao;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class FindByOr<T> extends FindBy<T>
{
    public static <T> FindBy<T> findBy(T t) {
        return new FindByOr<T>(t, null);
    }

    public static <T> FindBy<T> findBy(T t, Integer limit) {
        return new FindByOr<T>(t, limit);
    }

    public FindByOr(T t, Integer limit) {
        super(t, limit);
    }
}
