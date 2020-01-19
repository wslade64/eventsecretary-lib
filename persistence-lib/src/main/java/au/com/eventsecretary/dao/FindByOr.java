package au.com.eventsecretary.dao;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class FindByOr<T>
{
    private T t;

    public static <T> T findBy(T t)
    {
        return (T)new FindByOr<T>(t);
    }

    public FindByOr(T t)
    {
        this.t = t;
    }

    public T getTarget()
    {
        return t;
    }
}
