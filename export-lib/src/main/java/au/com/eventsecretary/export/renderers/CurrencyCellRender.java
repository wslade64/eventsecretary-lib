package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellBuilder;
import au.com.eventsecretary.export.CellRenderer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class CurrencyCellRender implements CellRenderer<Integer> {
    @Override
    public void render(Cell cell, Integer value, CellBuilder cellBuilder) {
        String sval = convertCurrency(value);

        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(Double.parseDouble(sval));
        cell.setCellStyle(cellBuilder.rowBuilder.sheetBuilder.workbookBuilder.currencyCellStyle);
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
