package au.com.eventsecretary.export;

import org.apache.poi.ss.usermodel.Cell;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public interface CellRenderer<T> {
    void render(Cell cell, T value, WorkbookBuilder workbookBuilder);
}
