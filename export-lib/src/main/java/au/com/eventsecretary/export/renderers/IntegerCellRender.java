package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellRenderer;
import au.com.eventsecretary.export.WorkbookBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class IntegerCellRender implements CellRenderer<Integer> {

    @Override
    public void render(Cell cell, Integer value, WorkbookBuilder workbookBuilder) {
        cell.setCellType(CellType.NUMERIC);
        if (value != null) {
            cell.setCellValue(value);
        }
        cell.setCellStyle(workbookBuilder.numericCellStyle);
    }
}
