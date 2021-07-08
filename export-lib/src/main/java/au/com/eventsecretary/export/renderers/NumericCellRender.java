package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellRenderer;
import au.com.eventsecretary.export.WorkbookBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class NumericCellRender implements CellRenderer<Double> {
    @Override
    public void render(Cell cell, Double value, WorkbookBuilder workbookBuilder) {
        cell.setCellType(CellType.NUMERIC);
        if (value != null)
        {
            cell.setCellValue(value);
        }
        cell.setCellStyle(workbookBuilder.numericCellStyle);
//        CellStyle cellStyle = cell.getCellStyle();
//        cellStyle.setDataFormat((short)1);
//        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
    }
}
