package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellRenderer;
import au.com.eventsecretary.export.WorkbookBuilder;
import org.apache.poi.ss.usermodel.Cell;

public class StringCellRender implements CellRenderer<String> {

    @Override
    public void render(Cell cell, String value, WorkbookBuilder workbookBuilder) {
        if (value == null) {
            value = "";
        }
        cell.setCellValue(value);
    }
}
