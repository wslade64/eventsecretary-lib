package au.com.eventsecretary.export.renderers;

import au.com.eventsecretary.export.CellRenderer;
import au.com.eventsecretary.export.WorkbookBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class FormulaCellRender implements CellRenderer<String> {

    @Override
    public void render(Cell cell, String value, WorkbookBuilder workbookBuilder) {
        cell.setCellFormula(value);

        CellStyle cellStyle = cell.getCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setDataFormat(workbookBuilder.dateFormat);
        cellStyle.setDataFormat((short)7);
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
    }
}
