package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellRenderer;
import au.com.eventsecretary.export.WorkbookBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class FormulaCellRender implements CellRenderer<String> {
    CellStyle cellStyle;

    public FormulaCellRender(CellStyle cellStyle) {
        this.cellStyle = cellStyle;
    }
    @Override
    public void render(Cell cell, String value, WorkbookBuilder workbookBuilder) {
        cell.setCellFormula(value);
        cell.setCellStyle(cellStyle);
    }
}
