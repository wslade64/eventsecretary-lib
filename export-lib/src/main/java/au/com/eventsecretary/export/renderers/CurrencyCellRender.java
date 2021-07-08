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
public class CurrencyCellRender implements CellRenderer<Object> {
    @Override
    public void render(Cell cell, Object value, WorkbookBuilder workbookBuilder) {
        Object dvalue;
        if (value == null) {
            dvalue = null;
        }
        else if (value instanceof Integer) {
            dvalue = Double.parseDouble(convertCurrency((Integer)value));
        } else if (value instanceof BigDecimal) {
            dvalue = ((BigDecimal)value).doubleValue();
        } else if (value instanceof String) {
            dvalue = Double.parseDouble((String)value);
        } else {
            dvalue = 666666;
        }

        cell.setCellStyle(workbookBuilder.currencyCellStyle);

        cell.setCellType(CellType.NUMERIC);
        if (dvalue == null) {
            cell.setCellValue((String)dvalue);
        } else {
            cell.setCellValue((double)dvalue);
        }
//        CellStyle cellStyle = cell.getCellStyle();
//        cellStyle.setDataFormat((short)7);
//        cellStyle.setAlignment(HorizontalAlignment.RIGHT);

//        cell.setCellStyle(workbookBuilder.currencyCellStyle);
    }

    public static String convertCurrency(int val)
    {
        String sval = String.format("%1$d.%2$02d", val / 100, Math.abs(val % 100));
        if (val > -100 && val < 0)
        {
            sval = "-" + sval;
        }
        return sval;
    }

}
