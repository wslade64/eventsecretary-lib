package au.com.eventsecretary.export;

import au.com.eventsecretary.UnexpectedSystemException;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class WorkbookBuilder {
    public static double CM_H = 0.197; // units of inches
    public static double CM_1 = 0.394; // units of inches
    public static double CM_2 = 0.787; // units of inches

    private final short FONT_SIZE = (short)11;
    protected final XSSFWorkbook workbook;
    public final CellStyle currencyCellStyle;
    public final CellStyle numericCellStyle;
    public final CellStyle dateStyle;
    public final CellStyle dateTimeStyle;
    public final CellStyle titleStyle;
    public final CellStyle headerStyle;
    public final CellStyle header2Style;
    public final CellStyle wrappedStyle;
    public final SimpleDateFormat sdf;
    public final SimpleDateFormat sdtf;
    public final Font normalFont;
    public final Font boldFont;
    public final Font titleFont;
    public final short dateFormat;
    public final short dateTimeFormat;
    public final CreationHelper helper;

    public WorkbookBuilder() {
        workbook = new XSSFWorkbook();
        POIXMLProperties.CoreProperties coreProperties = workbook.getProperties().getCoreProperties();
        coreProperties.setCreator("Event Secretary Pty Ltd");

        currencyCellStyle = workbook.createCellStyle();
        currencyCellStyle.setDataFormat((short)7);
        currencyCellStyle.setAlignment(HorizontalAlignment.RIGHT);

        numericCellStyle = workbook.createCellStyle();
        numericCellStyle.setDataFormat((short)1);
        numericCellStyle.setAlignment(HorizontalAlignment.RIGHT);

        dateStyle = workbook.createCellStyle();
        dateStyle.setAlignment(HorizontalAlignment.LEFT);
        dateFormat = workbook.createDataFormat().getFormat("dd-mm-yyyy");
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        dateStyle.setDataFormat(dateFormat);

        dateTimeStyle = workbook.createCellStyle();
        dateTimeStyle.setAlignment(HorizontalAlignment.LEFT);
        sdtf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        dateTimeFormat = workbook.createDataFormat().getFormat("dd-mm-yyyy hh:mm:ss");
        dateTimeStyle.setDataFormat(dateTimeFormat);

        boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldFont.setFontHeightInPoints(FONT_SIZE);

        normalFont = workbook.createFont();
        normalFont.setFontHeightInPoints(FONT_SIZE);

        headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFont(boldFont);

        header2Style = workbook.createCellStyle();
        header2Style.setAlignment(HorizontalAlignment.LEFT);
        header2Style.setFont(boldFont);

        titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 26);
        // 192E5B
        byte[] rgb = {0x19, 0x2E, 0x5B};
        ((XSSFFont)titleFont).setColor(new XSSFColor(rgb, null));

        titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.LEFT);
        titleStyle.setFont(titleFont);

        wrappedStyle = workbook.createCellStyle();
        wrappedStyle.setWrapText(true);

        helper = workbook.getCreationHelper();
    }


    public WorkbookBuilder title(String title) {
        POIXMLProperties.CoreProperties coreProperties = workbook.getProperties().getCoreProperties();
        coreProperties.setTitle(title);
        return this;
    }

    public SheetBuilder sheet(String sheetName) {
        return new SheetBuilder(this, sheetName);
    }

    public WorkbookBuilder gridOn() {
        for (int i = 0; i < workbook.getNumCellStyles(); i++) {
            XSSFCellStyle cellStyleAt = workbook.getCellStyleAt(i);
            cellStyleAt.setBorderBottom(BorderStyle.THIN);
            cellStyleAt.setBorderTop(BorderStyle.THIN);
            cellStyleAt.setBorderLeft(BorderStyle.THIN);
            cellStyleAt.setBorderRight(BorderStyle.THIN);
        }
        return this;
    }
    public void build(File outputFile) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
            workbook.write(fos);
            fos.close();
        } catch (IOException e) {
            throw new UnexpectedSystemException(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
