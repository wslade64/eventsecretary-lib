package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellBuilder;
import au.com.eventsecretary.export.CellRenderer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class StringCellRender implements CellRenderer<String> {

    @Override
    public void render(Cell cell, String value, CellBuilder cellBuilder) {
        if (value == null) {
            value = "";
        }
        cell.setCellType(CellType.STRING);
        cell.setCellValue(value);
    }
}
