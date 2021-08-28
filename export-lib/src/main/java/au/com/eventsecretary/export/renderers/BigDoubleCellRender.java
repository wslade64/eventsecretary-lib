package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellRenderer;
import au.com.eventsecretary.export.WorkbookBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.math.BigDecimal;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class BigDoubleCellRender implements CellRenderer<BigDecimal> {
    @Override
    public void render(Cell cell, BigDecimal value, WorkbookBuilder workbookBuilder) {
        cell.setCellType(CellType.NUMERIC);
        if (value != null)
        {
            cell.setCellValue(value.toPlainString());
        }
        cell.setCellStyle(workbookBuilder.numericCellStyle);
//        CellStyle cellStyle = cell.getCellStyle();
//        cellStyle.setDataFormat((short)1);
//        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
    }
}
