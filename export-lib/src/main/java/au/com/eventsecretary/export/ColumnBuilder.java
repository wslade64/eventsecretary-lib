package au.com.eventsecretary.export;

import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.export.renderers.*;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class ColumnBuilder {
    private final SheetBuilder sheetBuilder;
    private final SheetBuilder.Column column;

    public ColumnBuilder(SheetBuilder sheetBuilder) {
        this.sheetBuilder = sheetBuilder;
        column = new SheetBuilder.Column();
        sheetBuilder.columns.add(column);
    }

    public ColumnBuilder width(int size) {
        sheetBuilder.sheet.setColumnWidth(sheetBuilder.columns.indexOf(column), size);
        return this;
    }

    public ColumnBuilder sum() {
        column.sum = true;
        return this;
    }

    public ColumnBuilder stringFormat() {
        column.cellRenderer = new StringCellRender();
        return this;
    }

    public ColumnBuilder bigDecimalFormat() {
        column.cellRenderer = new BigDoubleCellRender();
        return this;
    }

    public ColumnBuilder integerFormat() {
        column.cellRenderer = new IntegerCellRender();
        return this;
    }

    public ColumnBuilder currencyFormat() {
        column.cellRenderer = new CurrencyCellRender();
        return this;
    }

    public ColumnBuilder numericFormat() {
        column.cellRenderer = new NumericCellRender();
        return this;
    }

    public ColumnBuilder dateFormat() {
        column.cellRenderer = new DateCellRender();
        return this;
    }

    public ColumnBuilder dateTimeFormat() {
        column.cellRenderer = new DateTimeCellRender();
        return this;
    }

    public ColumnBuilder stringFormat(ValueFormatter valueFormatter) {
        column.cellRenderer = new StringCellRender();
        column.valueFormatter = valueFormatter;
        return this;
    }

    public ColumnBuilder booleanFormat() {
        column.cellRenderer = new BooleanCellRender();
        return this;
    }

    public ColumnBuilder booleanFormat(String yesLabel, String noLabel) {
        column.cellRenderer = new BooleanCellRender(yesLabel, noLabel);
        return this;
    }

    public ColumnBuilder label(String label) {
        column.labels = new ArrayList<>();
        column.labels.add(label);
        return this;
    }

    public ColumnBuilder labels(List<String> labels) {
        column.labels = labels;
        return this;
    }

    public ColumnBuilder conditional(String conditional) {
        column.conditionals.add(conditional);
        return this;
    }

    public SheetBuilder end() {
        if (!column.labels.isEmpty() && column.cellRenderer == null) {
            throw new UnexpectedSystemException("CellRendererNotSet:" + column.labels.get(0));
        }
        if (!column.conditionals.isEmpty()) {
            if (sheetBuilder.exclude(column)) {
                column.include = Boolean.FALSE;
            }
            if (column.include == null) {
                if (sheetBuilder.conditional != null) {
                    column.include = new Boolean(resolveConditional(sheetBuilder.conditional));
                } else if (sheetBuilder.workbookBuilder.conditional != null) {
                    column.include = new Boolean(resolveConditional(sheetBuilder.workbookBuilder.conditional));
                }
            }
        }
        return sheetBuilder;
    }

    public ColumnBuilder dataValidation(String[] values, String title, String message) {
        int colIndex = sheetBuilder.columns.indexOf(column);
        DataValidationHelper validationHelper = sheetBuilder.sheet.getDataValidationHelper();
//        XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper((XSSFSheet) sheetBuilder.sheet);
        DataValidationConstraint explicitListConstraint = validationHelper.createExplicitListConstraint(values);
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, 1000, colIndex, colIndex);

        DataValidation validation = validationHelper.createValidation(explicitListConstraint, cellRangeAddressList);
        validation.setSuppressDropDownArrow(true);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox(title, message);
        validation.setShowErrorBox(true);
        sheetBuilder.sheet.addValidationData(validation);
        return this;
    }

    private boolean resolveConditional(Conditional conditional) {
        for (String aCondition : ((SheetBuilder.Column<?>)column).conditionals) {
            if (!conditional.accept(aCondition)) {
                return false;
            }
        }
        return true;
    }
}
