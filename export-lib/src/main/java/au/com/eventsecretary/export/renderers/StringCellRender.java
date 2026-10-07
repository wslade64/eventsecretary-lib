package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellRenderer;
import au.com.eventsecretary.export.WorkbookBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

public class StringCellRender implements CellRenderer<String> {
    private CellStyle cellStyle;

    public StringCellRender() {
    }

    public StringCellRender(CellStyle cellStyle) {
        this.cellStyle = cellStyle;
    }

    @Override
    public void render(Cell cell, String value, WorkbookBuilder workbookBuilder) {
        if (value == null) {
            value = "";
        }
        cell.setCellValue(value);
        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
    }
}
