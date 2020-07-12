package au.com.eventsecretary.dao;

/**
 * Used to as native criteria
 *
 * @author Warwick Slade
 */
public abstract class FindBy<T>
{
    private T t;
    private Integer limit;

    public FindBy(T t, Integer limit)
    {
        this.t = t;
        this.limit = limit;
    }

    public T getTarget()
    {
        return t;
    }
    public Integer getLimit() { return limit;}
}
