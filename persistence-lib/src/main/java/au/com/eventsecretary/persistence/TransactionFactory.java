package au.com.eventsecretary.persistence;

/**
 * A factory to create transactions.
 *
 * @author Warwick Slade
 */
public interface TransactionFactory
{
    public Transaction createTransaction();
}
