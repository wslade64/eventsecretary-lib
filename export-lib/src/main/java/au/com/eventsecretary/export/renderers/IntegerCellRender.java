package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellBuilder;
import au.com.eventsecretary.export.CellRenderer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class IntegerCellRender implements CellRenderer<Integer> {

    @Override
    public void render(Cell cell, Integer value, CellBuilder cellBuilder) {
        cell.setCellType(CellType.NUMERIC);
        if (value != null) {
            cell.setCellValue(value);
        }
        cell.setCellStyle(cellBuilder.rowBuilder.sheetBuilder.workbookBuilder.numericCellStyle);
    }
}
