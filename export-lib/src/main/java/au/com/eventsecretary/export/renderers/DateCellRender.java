package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellRenderer;
import au.com.eventsecretary.export.WorkbookBuilder;
import au.com.eventsecretary.simm.DateUtility;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Date;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class DateCellRender implements CellRenderer<Object> {
    @Override
    public void render(Cell cell, Object value, WorkbookBuilder workbookBuilder) {
        Date date;
        if (value instanceof Integer) {
            date = DateUtility.dateForDate((Integer) value);
        } else {
            date = (Date)value;
        }
        cell.setCellType(CellType.NUMERIC);
        if (value != null)
        {
            cell.setCellValue(date);
        }
        else
        {
            cell.setCellValue("");
        }

        cell.setCellStyle(workbookBuilder.dateStyle);

//        CellStyle cellStyle = cell.getCellStyle();
//        cellStyle.setAlignment(HorizontalAlignment.LEFT);
//        cellStyle.setDataFormat(workbookBuilder.dateFormat);
    }
}
