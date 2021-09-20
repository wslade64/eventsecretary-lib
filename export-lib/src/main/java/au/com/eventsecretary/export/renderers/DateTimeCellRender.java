package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.common.Timestamp;
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
public class DateTimeCellRender implements CellRenderer<Object> {
    @Override
    public void render(Cell cell, Object value, WorkbookBuilder workbookBuilder) {
        Date date;
        if (value instanceof Timestamp) {
            date = DateUtility.dateForTimestamp((Timestamp) value);
        } else {
            date = (Date)value;
        }
        cell.setCellType(CellType.NUMERIC);
        if (date != null)
        {
            cell.setCellValue(date);
        }
        else
        {
            cell.setCellValue("");
        }

        cell.setCellStyle(workbookBuilder.dateTimeStyle);
//        CellStyle cellStyle = cell.getCellStyle();
//        cellStyle.setAlignment(HorizontalAlignment.LEFT);
//        cellStyle.setDataFormat(workbookBuilder.dateTimeFormat);
    }
}
