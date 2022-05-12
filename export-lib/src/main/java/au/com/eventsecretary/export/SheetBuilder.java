package au.com.eventsecretary.export;

import au.com.eventsecretary.UnexpectedSystemException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static au.com.eventsecretary.export.WorkbookBuilder.CM_1;
import static org.apache.poi.ss.usermodel.PrintSetup.A4_TRANSVERSE_PAPERSIZE;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class SheetBuilder {
    public final WorkbookBuilder workbookBuilder;
    final Sheet sheet;
    List<String> exclusions;
    List<Column> columns = new ArrayList<>();

    static class Column<T> {
        List<String> labels;
        List<String> conditionals = new ArrayList<>();
        CellRenderer<T> cellRenderer;
        ValueFormatter<T, T> valueFormatter;
        boolean sum;
    }

    SheetBuilder(WorkbookBuilder workbookBuilder, String sheetName) {
        this.workbookBuilder = workbookBuilder;

        sheetName = sheetName.replaceAll("\\/", "-").replaceAll("\\*", "");
        if (sheetName.length() > 30) {
            sheetName = sheetName.substring(sheetName.length() - 30);
        }

        sheet = this.workbookBuilder.workbook.createSheet(sheetName);
        sheet.getPrintSetup().setPaperSize(A4_TRANSVERSE_PAPERSIZE);
    }

    public SheetBuilder landscape() {
        sheet.getPrintSetup().setLandscape(true);
        return this;
    }

    public SheetBuilder printArea(int column, int row, int width, int height ) {
        sheet.getWorkbook().setPrintArea(sheet.getWorkbook().getSheetIndex(sheet), column, column + width - 1, row, row + height - 1);
        return this;
    }

    public SheetBuilder repeatFirstRow() {
        sheet.setRepeatingRows(CellRangeAddress.valueOf("1:1"));
        return this;
    }

    public SheetBuilder margins(double left, double top, double right, double bottom) {
        this.sheet.setMargin(Sheet.LeftMargin, left);
        this.sheet.setMargin(Sheet.TopMargin, top);
        this.sheet.setMargin(Sheet.RightMargin, right);
        this.sheet.setMargin(Sheet.BottomMargin, bottom);
        return this;
    }

    public SheetBuilder header(String text) {
        Header header = this.sheet.getHeader();
        header.setCenter(text);
        this.sheet.setMargin(Sheet.HeaderMargin, CM_1);
        return this;
    }

    public SheetBuilder exclusions(List<String> exclusions) {
        this.exclusions = exclusions;
        return this;
    }

    public SheetBuilder column$(SheetBuilderFunction sheetBuilderFunction) {
        sheetBuilderFunction.build(this);
        return this;
    }

    public ColumnBuilder column() {
        return new ColumnBuilder(this);
    }

    public SheetBuilder column$(String label) {
        ColumnBuilder columnBuilder = new ColumnBuilder(this);
        columnBuilder.label(label);
        columnBuilder.stringFormat();
        return this;
    }

    public SheetBuilder column$(List<String> labels) {
//        if (!labels.isEmpty()) {
            ColumnBuilder columnBuilder = new ColumnBuilder(this);
            columnBuilder.labels(labels);
            columnBuilder.stringFormat();
//        }
        return this;
    }

    public SheetBuilder text(String text) {
        sheet.createRow(sheet.getLastRowNum() + 1).createCell(1).setCellValue(text);
        return this;
    }

    public MatrixBuilder matrix() {
        return new MatrixBuilder(this);
    }

    private void headers()
    {
        Row row = sheet.createRow(0);
        row.setRowStyle(workbookBuilder.headerStyle);
        int col = 0;

        for (Column column : columns)
        {
            if (exclude(column.conditionals)) {
                continue;
            }
            for (Object label : column.labels) {
                Cell cell = row.createCell(col++);
                cell.setCellValue((String)label);
                cell.setCellStyle(workbookBuilder.headerStyle);
            }
        }
    }

    public SheetBuilder freezePane(int col, int row) {
        sheet.createFreezePane(col, row);
        return this;
    }

    public SheetBuilder autoSize() {
        int columnCount = 0;
        for (Column column : columns) {
            columnCount += column.labels.size();
        }
        return autoSize(columnCount);
    }

    public SheetBuilder autoSize(int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
        return this;
    }

    public RowBuilder rows() {
        // TODO possible optional headers
        headers();
        return new RowBuilder(this, 1);
    }

    public WorkbookBuilder end() {
        return workbookBuilder;
    }

    public SheetBuilder logo(int col, int row, String pngName) {
        int pictureIdx;
        try {
            InputStream is = new FileInputStream(String.format("/etc/www/logos/%s.png", pngName));
            byte[] bytes = IOUtils.toByteArray(is);
            pictureIdx = workbookBuilder.workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            is.close();
        } catch (FileNotFoundException e) {
            throw new UnexpectedSystemException(e);
        } catch (IOException e) {
            throw new UnexpectedSystemException(e);
        }

        Drawing<?> drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = workbookBuilder.helper.createClientAnchor();
        anchor.setCol1(col);
        anchor.setRow1(row);
        Picture pict = drawing.createPicture(anchor, pictureIdx);

        pict.resize();
        return this;
    }

    public SheetBuilder turnOffGridLines() {
        sheet.setDisplayGridlines(false);
        return this;
    }

    boolean exclude(List<String> conditionals) {
        if (exclusions == null) {
            return false;
        }
        for (String conditional : conditionals) {
            if (exclusions.contains(conditional)) {
                return true;
            }
        }
        return false;
    }

    boolean test(String[] conditionals) {
        if (exclusions == null) {
            return true;
        }
        for (String conditional : conditionals) {
            if (exclusions.contains(conditional)) {
                return false;
            }
        }
        return true;
    }
}
