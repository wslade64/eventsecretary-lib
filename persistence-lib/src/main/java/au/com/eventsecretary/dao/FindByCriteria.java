package au.com.eventsecretary.dao;

/**
 * Used to as native criteria
 *
 * @author Warwick Slade
 */
public class FindByCriteria<T, C>
{
    private T t;
    private C c;

    public static <T, C> T findByCriteria(T t, C c)
    {
        return (T)new FindByCriteria<T, C>(t, c);
    }

    public FindByCriteria(T t, C c)
    {
        this.t = t;
        this.c = c;
    }

    public C getCriteria() {
        return c;
    }
    public T getTarget()
    {
        return t;
    }
}
