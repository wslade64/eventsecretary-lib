package au.com.eventsecretary.export;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class RowBuilder {
    public final SheetBuilder sheetBuilder;
    private final int firstRow;

    RowBuilder(SheetBuilder sheetBuilder, int firstRow) {
        this.sheetBuilder = sheetBuilder;
        this.firstRow = firstRow;
    }

    public <T> RowBuilder generate(List<T> valueList, CellWriter<T> cellWriter) {
        int rowIndex = firstRow;

        CellBuilder cellBuilder = new CellBuilder(this);

        for (T value : valueList) {
            Row row = sheetBuilder.sheet.createRow(rowIndex++);
            cellBuilder.row(row);
            cellWriter.write(cellBuilder, value);
        }
        lastRow();
        return this;
    }

    private void lastRow() {
        boolean atleastOne = false;
        for (SheetBuilder.Column column : sheetBuilder.columns) {
            if (sheetBuilder.exclude(column.conditionals)) {
                continue;
            }
            if (column.sum) {
                atleastOne = true;
                break;
            }
        }
        if (!atleastOne) {
            return;
        }

        int startRow = 2;
        int endRow = sheetBuilder.sheet.getLastRowNum() + 1;
        Row row = sheetBuilder.sheet.createRow(sheetBuilder.sheet.getLastRowNum() + 1);
        int col = 0;

        for (SheetBuilder.Column column : sheetBuilder.columns)
        {
            if (sheetBuilder.exclude(column.conditionals)) {
                continue;
            }
            for (Object label : column.labels) {
                Cell cell = row.createCell(col);
                if (column.sum) {
                    cell.setCellFormula(String.format("sum(%s%d:%s%d)", formatColumn(col), startRow, formatColumn(col), endRow));
                    cell.setCellStyle(sheetBuilder.workbookBuilder.currencyCellStyle);
                }
                col++;
            }
        }
        FormulaEvaluator evaluator = sheetBuilder.sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        evaluator.evaluateAll();
    }

    static String formatColumn(int column) {
        char[] chars;
        if (column <= 25) {
            chars = new char[1];
            chars[0] = (char)('A' + column);
        } else {
            chars = new char[2];
            chars[0] = (char)('A' + (column / 26 - 1));
            chars[1] = (char)('A' + column % 26);
        }
        return new String(chars);
    }

    public SheetBuilder end() {
        return sheetBuilder;
    }
}
