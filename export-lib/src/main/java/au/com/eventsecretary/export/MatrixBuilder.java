package au.com.eventsecretary.export;

import au.com.eventsecretary.export.renderers.CurrencyCellRender;
import au.com.eventsecretary.export.renderers.FormulaCellRender;
import au.com.eventsecretary.export.renderers.StringCellRender;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class MatrixBuilder {
    private final Sheet sheet;
    private final SheetBuilder sheetBuilder;

    public MatrixBuilder(SheetBuilder sheetBuilder) {
        this.sheet = sheetBuilder.sheet;
        this.sheetBuilder = sheetBuilder;
    }

    public class CellBuilder {
        private final Cell cell;
        private CellRenderer cellRenderer;

        private CellBuilder(Cell cell) {
            this.cell = cell;
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

        public CellBuilder header2() {
            cell.setCellStyle(sheetBuilder.workbookBuilder.header2Style);
            return this;
        }

        public CellBuilder value(Object value) {
            cellRenderer.render(cell, value, sheetBuilder.workbookBuilder);
            return this;
        }

        public CellBuilder borderTop() {
            cell.getCellStyle().setBorderTop(BorderStyle.THIN);
            return this;
        }

        public CellBuilder borderLeft() {
            cell.getCellStyle().setBorderLeft(BorderStyle.THIN);
            return this;
        }

        public CellBuilder borderRight() {
            cell.getCellStyle().setBorderRight(BorderStyle.THIN);
            return this;
        }

        public CellBuilder borderBottom() {
            cell.getCellStyle().setBorderBottom(BorderStyle.THIN);
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

    public CellBuilder cell(int colIndex, int rowIndex) {
        Row row = this.sheet.getRow(rowIndex);
        if (row == null) {
            row = this.sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        return new CellBuilder(cell);
    }

    public SheetBuilder end() {
        return sheetBuilder;
    }
}
