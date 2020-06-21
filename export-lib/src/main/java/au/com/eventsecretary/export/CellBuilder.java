package au.com.eventsecretary.export;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.Date;
import java.util.List;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class CellBuilder {
    final RowBuilder rowBuilder;

    private Row row;
    private int col;

    CellBuilder(RowBuilder rowBuilder) {
        this.rowBuilder = rowBuilder;
    }

    CellBuilder row(Row row) {
        this.row = row;
        this.col = 0;
        return this;
    }

    public CellBuilder _boolean(String val, String... conditionals)
    {
        if (!test(conditionals)) {
            return this;
        }

        val = val == null || val.equals("false") ? "No" : "Yes";

        Cell cell = row.createCell(col++);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(val);

        return this;
    }

    public CellBuilder string(String val, String... conditionals)
    {
        if (!test(conditionals)) {
            return this;
        }

        if (val == null) {
            val = "";
        }

        Cell cell = row.createCell(col++);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(val);

        return this;
    }

    public CellBuilder strings(List<String> vals, String... conditionals)
    {
        if (!test(conditionals)) {
            return this;
        }
        for (String val : vals) {
            Cell cell = row.createCell(col++);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(val);
        }
        return this;
    }

    public CellBuilder date(Date val, String... conditionals)
    {
        if (!test(conditionals)) {
            return this;
        }
        Cell cell = row.createCell(col++);
        cell.setCellType(CellType.NUMERIC);
        if (val != null)
        {
            cell.setCellValue(val);
        }
        else
        {
            cell.setCellValue("");
        }

        cell.setCellStyle(rowBuilder.sheetBuilder.workbookBuilder.dateStyle);
        return this;
    }

    public CellBuilder dateTime(Date val, String... conditionals) {
        if (!test(conditionals)) {
            return this;
        }
        Cell cell = row.createCell(col++);
        cell.setCellType(CellType.NUMERIC);
        if (val != null)
        {
            cell.setCellValue(val);
        }
        else
        {
            cell.setCellValue("");
        }

        cell.setCellStyle(rowBuilder.sheetBuilder.workbookBuilder.dateTimeStyle);
        return this;
    }

    public CellBuilder currency(int val, String... conditionals) {
        if (!test(conditionals)) {
            return this;
        }
        String sval = convertCurrency(val);

        Cell cell = row.createCell(col++);
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(Double.parseDouble(sval));
        cell.setCellStyle(rowBuilder.sheetBuilder.workbookBuilder.currencyCellStyle);
        return this;
    }

    public CellBuilder numeric(Double val) {
        Cell cell = row.createCell(col++);
        cell.setCellType(CellType.NUMERIC);
        if (val != null)
        {
            cell.setCellValue(val);
        }
        cell.setCellStyle(rowBuilder.sheetBuilder.workbookBuilder.numericCellStyle);
        return this;
    }

    public CellBuilder integer(Integer val, String... conditionals) {
        if (!test(conditionals)) {
            return this;
        }
        Cell cell = row.createCell(col++);
        cell.setCellType(CellType.NUMERIC);
        if (val != null)
        {
            cell.setCellValue(val);
        }
        cell.setCellStyle(rowBuilder.sheetBuilder.workbookBuilder.numericCellStyle);
        return this;
    }

    private boolean test(String... conditionals) {
        return rowBuilder.sheetBuilder.test(conditionals);
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
