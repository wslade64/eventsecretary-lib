package au.com.eventsecretary.export;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;

public class StyleBuilder {
    private WorkbookBuilder builder;
    private CellStyle customStyle;

    StyleBuilder(WorkbookBuilder builder, String name, String pattern) {
        this.builder = builder;

        customStyle = builder.workbook.createCellStyle();
        XSSFDataFormat dataFormat = builder.workbook.createDataFormat();
        customStyle.setDataFormat(dataFormat.getFormat(pattern));
        builder.addStyle(name, customStyle);
    }

    public StyleBuilder horizontalAlignment(HorizontalAlignment horizontalAlignment) {
        customStyle.setAlignment(horizontalAlignment);
        return this;
    }

    public StyleBuilder verticalAlignment(VerticalAlignment verticalAlignment) {
        customStyle.setVerticalAlignment(verticalAlignment);
        return this;
    }

    public StyleBuilder bold() {
        customStyle.setFont(builder.boldFont);
        return this;
    }

    public StyleBuilder font(String usageName) {
        customStyle.setFont(builder.getFont(usageName));
        return this;
    }

    public StyleBuilder wrap() {
        customStyle.setWrapText(true);
        return this;
    }

    public WorkbookBuilder end() {

        return builder;
    }
}
