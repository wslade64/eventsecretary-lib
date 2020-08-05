package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellBuilder;
import au.com.eventsecretary.export.CellRenderer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class BooleanCellRender implements CellRenderer<Object> {
    private final static String FALSE = "No";
    private final static String TRUE = "Yes";

    @Override
    public void render(Cell cell, Object value, CellBuilder cellBuilder) {
        if (value == null) {
            value = FALSE;
        } else if (value instanceof Boolean) {
            value = ((Boolean)value) ? TRUE : FALSE;
        } else if (value instanceof String) {
            value = ((String)value).equalsIgnoreCase("true") ? TRUE : FALSE;
        } else if (value instanceof Integer) {
            value = ((Integer)value) > 1 ? TRUE : FALSE;
        }
        cell.setCellType(CellType.STRING);
        cell.setCellValue(value.toString());
    }
}
