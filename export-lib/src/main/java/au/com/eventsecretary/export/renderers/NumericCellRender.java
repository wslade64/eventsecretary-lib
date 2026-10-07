package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellRenderer;
import au.com.eventsecretary.export.WorkbookBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;

import java.math.BigDecimal;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class NumericCellRender implements CellRenderer<Object> {
    private CellStyle cellStyle;

    public NumericCellRender(CellStyle cellStyle) {
        this.cellStyle = cellStyle;
    }
    public NumericCellRender() {
    }

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
                value = Double.valueOf(value.toString());
            } else if (value instanceof BigDecimal) {
              value = ((BigDecimal)value).doubleValue();
            } else if (!(value instanceof Double)) {
                value = null;
            }
        }
        if (value != null)
        {
            cell.setCellValue((Double)value);
        }

        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        } else {
            cell.setCellStyle(workbookBuilder.numericCellStyle);
        }
    }
}
