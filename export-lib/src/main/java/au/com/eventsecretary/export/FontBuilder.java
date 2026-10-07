package au.com.eventsecretary.export;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;

public class FontBuilder {
    private WorkbookBuilder builder;
    private Font font;

    FontBuilder(WorkbookBuilder builder, String name) {
        this.builder = builder;

        font = builder.workbook.createFont();
        builder.addFont(name, font);
    }

    public FontBuilder color(short color) {
        font.setColor(color);
        return this;
    }

    public FontBuilder bold() {
        font.setBold(true);
        return this;
    }

    public FontBuilder italic() {
        font.setItalic(true);
        return this;
    }

    public FontBuilder size(short height) {
        font.setFontHeight(height);
        return this;
    }

    public WorkbookBuilder end() {

        return builder;
    }
}
