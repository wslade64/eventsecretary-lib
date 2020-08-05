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
public class NumericCellRender implements CellRenderer<Double> {
    @Override
    public void render(Cell cell, Double value, CellBuilder cellBuilder) {
        cell.setCellType(CellType.NUMERIC);
        if (value != null)
        {
            cell.setCellValue(value);
        }
        cell.setCellStyle(cellBuilder.rowBuilder.sheetBuilder.workbookBuilder.numericCellStyle);
    }
}
