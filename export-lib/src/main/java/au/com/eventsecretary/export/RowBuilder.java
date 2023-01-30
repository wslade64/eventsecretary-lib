package au.com.eventsecretary.export;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;

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
        if (sheetBuilder.sheet.getLastRowNum() == 1) {
            return;
        }
        for (SheetBuilder.Column column : sheetBuilder.columns) {
            if (sheetBuilder.exclude(column)) {
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
            if (sheetBuilder.exclude(column)) {
                continue;
            }
            for (Object label : column.labels) {
                Cell cell = row.createCell(col);
                cell.setCellStyle(sheetBuilder.workbookBuilder.workbook.createCellStyle());
                cell.getCellStyle().setFont(sheetBuilder.workbookBuilder.normalFont);
                cell.getCellStyle().setBorderTop(BorderStyle.THIN);
                if (column.sum) {
                    cell.setCellFormula(String.format("sum(%s%d:%s%d)", formatColumn(col), startRow, formatColumn(col), endRow));
                    cell.getCellStyle().setDataFormat((short)7);
                    cell.getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
                }
                col++;
            }
        }
        FormulaEvaluator evaluator = sheetBuilder.sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        evaluator.evaluateAll();
    }

    public static String formatRange(int fromColumn, int fromRow, int toColumn, int toRow) {
        return String.format("%s:%s", formatCell(fromColumn, fromRow), formatCell(toColumn, toRow));
    }

    public static String formatCell(int column, int row) {
        return formatColumn(column) + formatRow(row);
    }

    public static String formatRow(int row) {
        return Integer.toString(row + 1);
    }

    public static String formatColumn(int column) {
        return CellReference.convertNumToColString(column);
//        char[] chars;
//        if (column <= 25) {
//            chars = new char[1];
//            chars[0] = (char)('A' + column);
//        } else {
//            chars = new char[2];
//            chars[0] = (char)('A' + (column / 26 - 1));
//            chars[1] = (char)('A' + column % 26);
//        }
//        return new String(chars);
    }

    public SheetBuilder end() {
        return sheetBuilder;
    }
}
