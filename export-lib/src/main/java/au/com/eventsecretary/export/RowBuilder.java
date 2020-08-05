package au.com.eventsecretary.export;

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
        return this;
    }

    public SheetBuilder end() {
        return sheetBuilder;
    }
}
