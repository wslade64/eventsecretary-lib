package au.com.eventsecretary.export;

import au.com.auspost.simm.model.Attribute;
import au.com.auspost.simm.model.ComplexType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;

import static au.com.eventsecretary.simm.ExtensionUtils.alias;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class SheetBuilder {
    final WorkbookBuilder workbookBuilder;
    final Sheet sheet;
    List<String> exclusions;
    List<Column> columns = new ArrayList<>();

    static class Column {
        String label;
        List<String> conditionals = new ArrayList<>();
    }

    SheetBuilder(WorkbookBuilder workbookBuilder, String sheetName) {
        this.workbookBuilder = workbookBuilder;
        sheet = this.workbookBuilder.workbook.createSheet(sheetName);
    }

    public SheetBuilder exclusions(List<String> exclusions) {
        this.exclusions = exclusions;
        return this;
    }

    public ColumnBuilder column() {
        return new ColumnBuilder(this);
    }

    public SheetBuilder column$(String label) {
        ColumnBuilder columnBuilder = new ColumnBuilder(this);
        columnBuilder.label(label);
        return this;
    }

    public SheetBuilder columns(List<String> columns) {
        for (String column : columns) {
            column$(column);
        }
        return this;
    }

    public SheetBuilder columns(ComplexType complexType) {
        if (complexType == null) {
            return this;
        }
        for (Attribute attribute : complexType.getAttributes()) {
            column$(alias(attribute));
        }
        return this;
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
            Cell cell = row.createCell(col++);
            cell.setCellValue(column.label);
            cell.setCellStyle(workbookBuilder.headerStyle);
        }
    }

    public SheetBuilder freezePane(int col, int row) {
        sheet.createFreezePane(col, row);
        return this;
    }

    public SheetBuilder autoSize() {
        for (int i = 0; i < columns.size(); i++) {
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

    boolean exclude(List<String> conditionals) {
        for (String conditional : conditionals) {
            if (exclusions.contains(conditional)) {
                return true;
            }
        }
        return false;
    }

    boolean test(String[] conditionals) {
        for (String conditional : conditionals) {
            if (exclusions.contains(conditional)) {
                return false;
            }
        }
        return true;
    }

}
