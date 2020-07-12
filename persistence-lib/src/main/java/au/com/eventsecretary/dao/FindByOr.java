package au.com.eventsecretary.dao;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class FindByOr<T> extends FindBy<T>
{
    public static <T> T findBy(T t) {
        return (T)new FindByOr<T>(t, null);
    }

    public static <T> T findBy(T t, Integer limit) {
        return (T)new FindByOr<T>(t, limit);
    }

    public FindByOr(T t, Integer limit) {
        super(t, limit);
    }
}
