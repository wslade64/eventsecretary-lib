package au.com.eventsecretary.export;

import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.export.renderers.BigDoubleCellRender;
import au.com.eventsecretary.export.renderers.BooleanCellRender;
import au.com.eventsecretary.export.renderers.CurrencyCellRender;
import au.com.eventsecretary.export.renderers.DateCellRender;
import au.com.eventsecretary.export.renderers.DateTimeCellRender;
import au.com.eventsecretary.export.renderers.IntegerCellRender;
import au.com.eventsecretary.export.renderers.NumericCellRender;
import au.com.eventsecretary.export.renderers.StringCellRender;

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

    private boolean resolveConditional(Conditional conditional) {
        for (String aCondition : ((SheetBuilder.Column<?>)column).conditionals) {
            if (!conditional.accept(aCondition)) {
                return false;
            }
        }
        return true;
    }
}
