package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellRenderer;
import au.com.eventsecretary.export.WorkbookBuilder;
import org.apache.poi.ss.usermodel.Cell;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class FormulaCellRender implements CellRenderer<String> {

    @Override
    public void render(Cell cell, String value, WorkbookBuilder workbookBuilder) {
        cell.setCellFormula(value);
        cell.setCellStyle(workbookBuilder.currencyCellStyle);
    }
}
