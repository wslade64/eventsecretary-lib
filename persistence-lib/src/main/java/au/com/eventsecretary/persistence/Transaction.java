package au.com.eventsecretary.persistence;

import java.util.List;

/**
 *
 * @author Warwick Slade
 */
public interface Transaction
{
    <T> void enroll(T object);
    <T> void enrollForDelete(T object);
    <T> List<T> enrolled();

    void commit();

    void rollback();
}
