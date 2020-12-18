package au.com.eventsecretary.export;

import au.com.eventsecretary.UnexpectedSystemException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class CellBuilder {
    public final RowBuilder rowBuilder;

    private Row row;
    private int sheetColumn;
    private int attributeColumn;

    CellBuilder(RowBuilder rowBuilder) {
        this.rowBuilder = rowBuilder;
    }

    CellBuilder row(Row row) {
        this.row = row;
        this.sheetColumn = 0;
        this.attributeColumn = 0;
        return this;
    }

    public <T> CellBuilder column(T value) {
        SheetBuilder.Column<T> column = attributeColumn();
        if (column != null) {
            Cell cell = createCell();
            if (column.valueFormatter != null) {
                value = column.valueFormatter.format(value);
            }
            column.cellRenderer.render(cell, value, this.rowBuilder.sheetBuilder.workbookBuilder);
        }
        return this;
    }

    public <T> CellBuilder column(List<T> values) {
        SheetBuilder.Column<T> column = attributeColumn();

        if (column != null) {
            values.forEach(value -> {
                Cell cell = createCell();
                column.cellRenderer.render(cell, value, this.rowBuilder.sheetBuilder.workbookBuilder);
            });
        }
        return this;
    }

    public <T> CellBuilder columns(List<T> values) {
        values.forEach(value -> {
            SheetBuilder.Column<T> column = attributeColumn();
            if (column != null) {
                Cell cell = createCell();
                column.cellRenderer.render(cell, value, this.rowBuilder.sheetBuilder.workbookBuilder);
            }
        });
        return this;
    }

    private Cell createCell()  {
        Cell cell = row.createCell(sheetColumn++);
        cell.setCellStyle(rowBuilder.sheetBuilder.workbookBuilder.workbook.createCellStyle());
        cell.getCellStyle().setFont(rowBuilder.sheetBuilder.workbookBuilder.normalFont);
        return cell;
    }

    private <T> SheetBuilder.Column<T> attributeColumn() {
        if (attributeColumn >= rowBuilder.sheetBuilder.columns.size()) {
            throw new UnexpectedSystemException("sheet:" + rowBuilder.sheetBuilder.sheet.getSheetName() + ":toManyDataColumns");
        }
        SheetBuilder.Column<T> column = rowBuilder.sheetBuilder.columns.get(attributeColumn++);
        if (rowBuilder.sheetBuilder.exclude(column.conditionals)) {
            return null;
        }
        return column;
    }
}
