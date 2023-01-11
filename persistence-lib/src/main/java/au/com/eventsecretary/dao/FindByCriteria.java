package au.com.eventsecretary.dao;

/**
 * Used to as native criteria
 *
 * @author Warwick Slade
 */
public class FindByCriteria<T, C> extends FindBy<T>
{
    private C c;

    public static <T, C> FindBy<T> findByCriteria(T t, C c)
    {
        return new FindByCriteria<T, C>(t, c);
    }
    public static <T, C> FindBy<T> findByCriteria(T t, C c, Integer limit) {
        return new FindByCriteria<T, C>(t, c, limit);
    }

    public FindByCriteria(T t, C c)
    {
        this(t, c, null);
    }

    public FindByCriteria(T t, C c, Integer limit)
    {
        super(t, limit);
        this.c = c;
    }

    public C getCriteria() {
        return c;
    }
}
