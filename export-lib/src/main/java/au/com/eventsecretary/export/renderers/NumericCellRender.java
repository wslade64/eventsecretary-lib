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
public class NumericCellRender implements CellRenderer<Object> {
    @Override
    public void render(Cell cell, Object value, WorkbookBuilder workbookBuilder) {
        cell.setCellType(CellType.NUMERIC);
        if (value != null) {
            if (value instanceof String) {
                try {
                    value = Double.parseDouble((String)value);
                } catch (NumberFormatException e) {
                    value = null;
                }
            } else if (value instanceof Integer) {
                value = new Double(value.toString());
            } else if (!(value instanceof Double)) {
                value = null;
            }
        }
        if (value != null)
        {
            cell.setCellValue((Double)value);
        }
        cell.setCellStyle(workbookBuilder.numericCellStyle);
//        CellStyle cellStyle = cell.getCellStyle();
//        cellStyle.setDataFormat((short)1);
//        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
    }
}
