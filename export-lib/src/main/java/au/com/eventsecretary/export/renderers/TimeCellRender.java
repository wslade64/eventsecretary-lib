package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellRenderer;
import au.com.eventsecretary.export.WorkbookBuilder;
import au.com.eventsecretary.simm.DateUtility;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class TimeCellRender implements CellRenderer<Object> {
    @Override
    public void render(Cell cell, Object value, WorkbookBuilder workbookBuilder) {
        cell.setCellType(CellType.NUMERIC);
        if (value != null)
        {
            if (value instanceof Integer) {
                cell.setCellValue(DateUtility.shorterTime((int)value));
            }
        }
        else
        {
            cell.setCellValue("");
        }
        cell.setCellStyle(workbookBuilder.timeStyle);
    }
}
