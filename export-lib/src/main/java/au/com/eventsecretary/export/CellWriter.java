package au.com.eventsecretary.export;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public interface CellWriter<T> {
    void write(CellBuilder cellBuilder, T value);
}
