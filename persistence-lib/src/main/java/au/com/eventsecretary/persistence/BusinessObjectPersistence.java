package au.com.eventsecretary.persistence;

import java.util.List;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public interface BusinessObjectPersistence
{
    String CONTAINS = "`%s`";

    <T> void storeObject(T object);

    <T> T findObject(T search);

    <T> List<T> findObjects(T search);

    <T> void deleteObject(T object);
}
