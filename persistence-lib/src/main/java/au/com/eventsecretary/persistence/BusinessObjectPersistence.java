package au.com.eventsecretary.persistence;

import au.com.eventsecretary.dao.FindBy;

import java.util.List;
import java.util.regex.Pattern;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public interface BusinessObjectPersistence
{
    String CONTAINS = "`%s`";
    String MATCHES_IGNORE_CASE = "~%s~";

    static String matchesIgnoreCase(String text) {
        return String.format(MATCHES_IGNORE_CASE, escapeRegex(text));
    }

    static String containsIgnoreCase(String text) {
        return String.format(CONTAINS, escapeRegex(text));
    }

    static String escapeRegex(String someText) {
        return Pattern.quote(someText);
    }


    <T> void storeObject(T object);

    <T> T findObject(FindBy<T> findBy);

    <T> T findObject(T search);

    <T> List<T> findObjects(FindBy<T> search);

    <T> List<T> findObjects(T search);

    <T> void deleteObject(T object);
}
