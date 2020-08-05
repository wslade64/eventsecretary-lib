package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellBuilder;
import au.com.eventsecretary.export.CellRenderer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Date;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class DateTimeCellRender implements CellRenderer<Date> {
    @Override
    public void render(Cell cell, Date value, CellBuilder cellBuilder) {
        cell.setCellType(CellType.NUMERIC);
        if (value != null)
        {
            cell.setCellValue(value);
        }
        else
        {
            cell.setCellValue("");
        }

        cell.setCellStyle(cellBuilder.rowBuilder.sheetBuilder.workbookBuilder.dateTimeStyle);
    }
}
