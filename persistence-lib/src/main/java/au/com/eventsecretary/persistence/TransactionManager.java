package au.com.eventsecretary.persistence;

import au.com.eventsecretary.UnexpectedSystemException;

import java.util.ArrayList;
import java.util.List;

/**
 * When working with Business Objects there is no notion of a transactional commit.
 * However consistency is still important. As such all objects should be persisted,
 * once a business action is complete.
 *
 *
 * @author Warwick Slade
 */
public final class TransactionManager
{
    private static ThreadLocal<Transaction> local;

    private TransactionManager()
    {}

    public static void begin(Transaction context)
    {
        if (local == null)
        {
            local = new ThreadLocal<Transaction>();
            local.set(context);
        }
        else
        {
            throw new UnexpectedSystemException("Transaction already commenced.");
        }
    }

    public static <T> void enroll(T object)
    {
        if (local == null)
        {
            throw new UnexpectedSystemException("Transaction has not begun.");
        }
        local.get().enroll(object);
    }

    public static <T> void enrollForDelete(T object)
    {
        if (local == null)
        {
            throw new UnexpectedSystemException("Transaction has not begun.");
        }
        local.get().enrollForDelete(object);
    }

    public static void flush()
    {
        if (local == null)
        {
            throw new UnexpectedSystemException("Transaction has not begun.");
        }
        local.get().commit();
    }

    public static void commit()
    {
        if (local == null)
        {
            throw new UnexpectedSystemException("Transaction has not begun.");
        }
        try
        {
            local.get().commit();
        }
        finally
        {
            local = null;
        }
    }

    public static void rollback()
    {
        if (local == null)
        {
            throw new UnexpectedSystemException("Transaction has not begun.");
        }
        try
        {
            local.get().rollback();
        }
        finally
        {
            local = null;
        }
    }

    public static <T> List<T> enrolled()
    {
        if (local == null)
        {
            return new ArrayList();
        }
        return local.get().enrolled();
    }
}
