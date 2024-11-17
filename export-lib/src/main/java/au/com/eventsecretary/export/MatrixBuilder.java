package au.com.eventsecretary.export;

import au.com.eventsecretary.export.renderers.CurrencyCellRender;
import au.com.eventsecretary.export.renderers.FormulaCellRender;
import au.com.eventsecretary.export.renderers.StringCellRender;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import java.util.function.Consumer;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class MatrixBuilder {
    private final Sheet sheet;
    private final SheetBuilder sheetBuilder;
    private int rowIndex = 0;

    public MatrixBuilder(SheetBuilder sheetBuilder) {
        this(sheetBuilder, 0);
    }

    public MatrixBuilder(SheetBuilder sheetBuilder, int startRow) {
        this.sheet = sheetBuilder.sheet;
        this.sheetBuilder = sheetBuilder;
        this.rowIndex = startRow;
    }


    public MatrixBuilder width(int colIndex, int size) {
        this.sheet.setColumnWidth(colIndex, size);
        return this;
    }

    public MatrixBuilder height(int size) {
        Row row = this.sheet.getRow(rowIndex);
        if (row == null) {
            row = this.sheet.createRow(rowIndex);
        }
        row.setHeight((short)size);
        return this;
    }

    public class CellBuilder {
        private final Cell cell;
        private CellRenderer cellRenderer;

        private CellBuilder(Cell cell) {
            this.cell = cell;
            this.cell.setCellStyle(sheetBuilder.workbookBuilder.normalStyle);
        }

        public CellBuilder stringFormat() {
            cellRenderer = new StringCellRender();
            return this;
        }

        public CellBuilder currencyFormat() {
            cellRenderer = new CurrencyCellRender();
            return this;
        }

        public CellBuilder formulaFormat() {
            cellRenderer = new FormulaCellRender();
            return this;
        }

        public CellBuilder title() {
            cell.setCellStyle(sheetBuilder.workbookBuilder.titleStyle);
            cell.getRow().setHeightInPoints(sheetBuilder.workbookBuilder.titleFont.getFontHeightInPoints() + 5);
            return this;
        }

        public CellBuilder header() {
            cell.setCellStyle(sheetBuilder.workbookBuilder.headerStyle);
            return this;
        }

        public CellBuilder centered() {
            cell.setCellStyle(sheetBuilder.workbookBuilder.centeredStyle);
            return this;
        }

        public CellBuilder important() {
            cell.setCellStyle(sheetBuilder.workbookBuilder.importantStyle);
            return this;
        }

        public CellBuilder value(Object value) {
            cellRenderer.render(cell, value, sheetBuilder.workbookBuilder);
            return this;
        }

        public CellBuilder wrap() {
            // Something about setting the font results in the wrap text not working.
            cell.setCellStyle(sheetBuilder.workbookBuilder.workbook.createCellStyle());
            cell.getCellStyle().setWrapText(true);
            return this;
        }

        public CellBuilder borderTop() {
            RegionUtil.setBorderTop(BorderStyle.THIN, new CellRangeAddress(cell.getRow().getRowNum(), cell.getRow().getRowNum(), cell.getColumnIndex(), cell.getColumnIndex()), sheet);
            return this;
        }

        public CellBuilder borderLeft() {
            RegionUtil.setBorderLeft(BorderStyle.THIN, new CellRangeAddress(cell.getRow().getRowNum(), cell.getRow().getRowNum(), cell.getColumnIndex(), cell.getColumnIndex()), sheet);
            return this;
        }

        public CellBuilder borderRight() {
            RegionUtil.setBorderRight(BorderStyle.THIN, new CellRangeAddress(cell.getRow().getRowNum(), cell.getRow().getRowNum(), cell.getColumnIndex(), cell.getColumnIndex()), sheet);
            return this;
        }

        public CellBuilder borderBottom() {
            RegionUtil.setBorderBottom(BorderStyle.THIN, new CellRangeAddress(cell.getRow().getRowNum(), cell.getRow().getRowNum(), cell.getColumnIndex(), cell.getColumnIndex()), sheet);
            return this;
        }

        public CellBuilder span(int width, int height) {
            sheet.addMergedRegion(new CellRangeAddress(
                    cell.getRowIndex(), //first row (0-based)
                    cell.getRowIndex()+ height - 1, //last row  (0-based)
                    cell.getColumnIndex(), //first column (0-based)
                    cell.getColumnIndex() + width - 1  //last column  (0-based)
            ));
            return this;
        }

        public MatrixBuilder end() {
            return MatrixBuilder.this;
        }
    }

    public CellBuilder cell(int colIndex) {
        Row row = this.sheet.getRow(rowIndex);
        if (row == null) {
            row = this.sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        this.sheetBuilder.lastIndex = colIndex;
        return new CellBuilder(cell);
    }

    public MatrixBuilder nextRow() {
        rowIndex++;
        return this;
    }

    public MatrixBuilder nextRow(Consumer<Integer> notify) {
        rowIndex++;
        notify.accept(rowIndex);
        return this;
    }

    public MatrixBuilder autoSize(int column) {
        sheet.autoSizeColumn(column);
        return this;
    }

    public SheetBuilder end() {
        return sheetBuilder;
    }
}
