package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellRenderer;
import au.com.eventsecretary.export.WorkbookBuilder;
import org.apache.poi.ss.usermodel.Cell;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class BooleanCellRender implements CellRenderer<Object> {
    private final String FALSE;
    private final String TRUE;

    public BooleanCellRender() {
        this("Yes", "No");
    }

    public BooleanCellRender(String yesLabel, String noLabel) {
        this.TRUE = yesLabel;
        this.FALSE = noLabel;
    }

    @Override
    public void render(Cell cell, Object value, WorkbookBuilder workbookBuilder) {
        if (value == null) {
            value = "";
        } else if (value instanceof Boolean) {
            value = ((Boolean)value) ? TRUE : FALSE;
        } else if (value instanceof String) {
            value = ((String)value).equalsIgnoreCase("true") ? TRUE : FALSE;
        } else if (value instanceof Integer) {
            value = ((Integer)value) > 1 ? TRUE : FALSE;
        }
        cell.setCellValue(value.toString());
    }
}
