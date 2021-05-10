package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellRenderer;
import au.com.eventsecretary.export.WorkbookBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

public class IntegerCellRender implements CellRenderer<Object> {

    @Override
    public void render(Cell cell, Object value, WorkbookBuilder workbookBuilder) {
        if (value != null) {
            if (value instanceof String) {
                try {
                    value = Integer.parseInt((String)value);
                } catch (NumberFormatException e) {
                    value = null;
                }
            } else if (!(value instanceof Integer)) {
                value = null;
            }
        }
        cell.setCellType(CellType.NUMERIC);
        if (value != null) {
            cell.setCellValue((Integer)value);
        }

        CellStyle cellStyle = cell.getCellStyle();
        cellStyle.setDataFormat((short)1);
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
    }
}
