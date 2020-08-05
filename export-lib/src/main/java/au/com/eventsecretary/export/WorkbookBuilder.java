package au.com.eventsecretary.export;

import au.com.eventsecretary.UnexpectedSystemException;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
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
    protected final XSSFWorkbook workbook;
    public final CellStyle currencyCellStyle;
    public final CellStyle numericCellStyle;
    public final CellStyle dateStyle;
    public final CellStyle dateTimeStyle;
    public final CellStyle headerStyle;
    public final SimpleDateFormat sdf;
    public final SimpleDateFormat sdtf;
    public final Font boldFont;

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
        short df = workbook.createDataFormat().getFormat("dd-mm-yyyy");
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        dateStyle.setDataFormat(df);

        dateTimeStyle = workbook.createCellStyle();
        dateTimeStyle.setAlignment(HorizontalAlignment.LEFT);
        sdtf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        short dtf = workbook.createDataFormat().getFormat("dd-mm-yyyy hh:mm:ss");
        dateTimeStyle.setDataFormat(dtf);

        boldFont = workbook.createFont();
        boldFont.setBold(true);

        headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFont(boldFont);
    }

    public WorkbookBuilder title(String title) {
        POIXMLProperties.CoreProperties coreProperties = workbook.getProperties().getCoreProperties();
        coreProperties.setTitle(title);
        return this;
    }

    public SheetBuilder sheet(String sheetName) {
        return new SheetBuilder(this, sheetName);
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
