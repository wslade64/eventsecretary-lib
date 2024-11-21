package au.com.eventsecretary.export;

import au.com.eventsecretary.UnexpectedSystemException;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 *
 * @author Warwick Slade
 */
public class WorkbookBuilder implements FileBuilder {
    public static double CM_H = 0.197; // units of inches
    public static double CM_1 = 0.394; // units of inches
    public static double CM_2 = 0.787; // units of inches

    private final static short FONT_SIZE = (short)11;
    protected final XSSFWorkbook workbook;
    public final CellStyle normalStyle;
    public final CellStyle currencyCellStyle;
    public final CellStyle numericCellStyle;
    public final CellStyle dateStyle;
    public final CellStyle dateTimeStyle;
    public final CellStyle timeStyle;
    public final CellStyle titleStyle;
    public final CellStyle headerStyle;
    public final CellStyle headerHighlightStyle;
    public final CellStyle importantStyle;
    public final CellStyle highlightStyle;
    public final CellStyle wrappedStyle;
    public final CellStyle formulaStyle;
    public final CellStyle centeredStyle;
    public final SimpleDateFormat sdf;
    public final SimpleDateFormat sdtf;
    public final SimpleDateFormat stf;
    public final Font normalFont;
    public final Font boldFont;
    public final Font titleFont;
    public final short dateFormat;
    public final short dateTimeFormat;
    public final short timeFormat;
    public final CreationHelper helper;
    public Conditional conditional;

    public WorkbookBuilder() {
        this(FONT_SIZE);
    }
    public WorkbookBuilder(short fontSize) {
        workbook = new XSSFWorkbook();
        POIXMLProperties.CoreProperties coreProperties = workbook.getProperties().getCoreProperties();
        coreProperties.setCreator("Event Secretary Pty Ltd");

        short highlightColor = IndexedColors.GREY_25_PERCENT.getIndex();

        normalFont = workbook.createFont();
        normalFont.setFontHeightInPoints(fontSize);

        boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldFont.setFontHeightInPoints(fontSize);

        numericCellStyle = workbook.createCellStyle();
        numericCellStyle.setDataFormat((short)1);
        numericCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        numericCellStyle.setFont(normalFont);

        centeredStyle = workbook.createCellStyle();
        centeredStyle.setDataFormat((short)1);
        centeredStyle.setAlignment(HorizontalAlignment.CENTER);
        centeredStyle.setFont(normalFont);

        dateStyle = workbook.createCellStyle();
        dateStyle.setAlignment(HorizontalAlignment.LEFT);
        dateFormat = workbook.createDataFormat().getFormat("dd-mm-yyyy");
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        dateStyle.setDataFormat(dateFormat);
        dateStyle.setFont(normalFont);

        dateTimeStyle = workbook.createCellStyle();
        dateTimeStyle.setAlignment(HorizontalAlignment.LEFT);
        sdtf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        dateTimeFormat = workbook.createDataFormat().getFormat("dd-mm-yyyy hh:mm:ss");
        dateTimeStyle.setDataFormat(dateTimeFormat);
        dateTimeStyle.setFont(normalFont);

        timeStyle = workbook.createCellStyle();
        timeStyle.setAlignment(HorizontalAlignment.LEFT);
        stf = new SimpleDateFormat("hh:mma");
        timeFormat = workbook.createDataFormat().getFormat("hh:mma");
        timeStyle.setDataFormat(timeFormat);
        timeStyle.setFont(normalFont);


        formulaStyle = workbook.createCellStyle();
        formulaStyle.setAlignment(HorizontalAlignment.RIGHT);
        formulaStyle.setDataFormat((short)7);

        headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFont(boldFont);

        headerHighlightStyle = workbook.createCellStyle();
        headerHighlightStyle.setWrapText(true);
        headerHighlightStyle.cloneStyleFrom(headerStyle);
        headerHighlightStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerHighlightStyle.setFillForegroundColor(highlightColor);
        headerHighlightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        importantStyle = workbook.createCellStyle();
        importantStyle.setAlignment(HorizontalAlignment.LEFT);
        importantStyle.setWrapText(true);
        importantStyle.setFont(boldFont);

        highlightStyle = workbook.createCellStyle();
        highlightStyle.setWrapText(true);
        highlightStyle.setAlignment(HorizontalAlignment.LEFT);
        highlightStyle.setFont(boldFont);
        highlightStyle.setFillForegroundColor(highlightColor);
        highlightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        highlightStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 26);
        // #0E78BE
        byte[] rgb = {0x0E, 0x78, -66};
        ((XSSFFont)titleFont).setColor(new XSSFColor(rgb, null));

        titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.LEFT);
        titleStyle.setFont(titleFont);

        wrappedStyle = workbook.createCellStyle();
        wrappedStyle.setWrapText(true);
        wrappedStyle.setFont(normalFont);

        helper = workbook.getCreationHelper();

        currencyCellStyle = workbook.createCellStyle();
        currencyCellStyle.setDataFormat((short)7);
        currencyCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        currencyCellStyle.setFont(normalFont);

        normalStyle = workbook.createCellStyle();
        normalStyle.setFont(normalFont);
    }

    public WorkbookBuilder conditional(Conditional conditional) {
        this.conditional = conditional;
        return this;
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

    public String build(File outputFile) {
        try (FileOutputStream fos = new FileOutputStream(outputFile)){
            workbook.write(fos);
            return "xlsx";
        } catch (IOException e) {
            throw new UnexpectedSystemException(e);
        }
    }
}
