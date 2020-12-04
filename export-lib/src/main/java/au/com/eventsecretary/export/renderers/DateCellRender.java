package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellRenderer;
import au.com.eventsecretary.export.WorkbookBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.util.Date;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class DateCellRender implements CellRenderer<Date> {
    @Override
    public void render(Cell cell, Date value, WorkbookBuilder workbookBuilder) {
        cell.setCellType(CellType.NUMERIC);
        if (value != null)
        {
            cell.setCellValue(value);
        }
        else
        {
            cell.setCellValue("");
        }

        cell.setCellStyle(workbookBuilder.dateStyle);

        CellStyle cellStyle = cell.getCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setDataFormat(workbookBuilder.dateFormat);
    }
}
