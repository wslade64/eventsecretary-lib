package au.com.eventsecretary.export;

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

    public ColumnBuilder label(String label) {
        column.label = label;
        return this;
    }

    public ColumnBuilder conditional(String conditional) {
        column.conditionals.add(conditional);
        return this;
    }

    public SheetBuilder end() {
        return sheetBuilder;
    }
}
