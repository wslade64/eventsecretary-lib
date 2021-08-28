package au.com.eventsecretary.export;

import au.com.eventsecretary.UnexpectedSystemException;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.lang3.tuple.Pair.of;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class ExportBuilder {
    private List<Pair<String, FileBuilder>> fileBuilders = new ArrayList<>();

    public WorkbookBuilder workbook() {
        WorkbookBuilder workbookBuilder = new WorkbookBuilder();
        fileBuilders.add(of("", workbookBuilder));
        return workbookBuilder;
    }

    public WorkbookBuilder workbook(String name) {
        WorkbookBuilder workbookBuilder = new WorkbookBuilder();
        fileBuilders.add(of(name,workbookBuilder));
        return workbookBuilder;
    }


    public XmlBuilder xml(Object target) {
        XmlBuilder xmlBuilder = new XmlBuilder(target);
        fileBuilders.add(of("", xmlBuilder));
        return xmlBuilder;
    }

    public XmlBuilder xml(String name, Object target) {
        XmlBuilder xmlBuilder = new XmlBuilder(target);
        fileBuilders.add(of(name, xmlBuilder));
        return xmlBuilder;
    }

    public String build(File file) {
        if (fileBuilders.size() == 1) {
            return fileBuilders.get(0).getRight().build(file);
        } else {
            try (FileOutputStream out = new FileOutputStream(file)) {
                ZipOutputStream zipOut = new ZipOutputStream(out);
                for (Pair<String, FileBuilder> fileBuilder : fileBuilders) {
                    ZipEntry zipEntry = new ZipEntry(fileBuilder.getLeft());
                    zipOut.putNextEntry(zipEntry);
                    File tmpFile = null;
                    try {
                        tmpFile = File.createTempFile("export-", ".tmp");
                        fileBuilder.getRight().build(tmpFile);
                        try (FileInputStream fis = new FileInputStream(tmpFile)) {
                            IOUtils.copy(fis, zipOut);
                        }
                    } finally {
                        if (tmpFile != null) {
                            tmpFile.delete();
                        }
                    }
                    zipOut.closeEntry();
                }
                zipOut.close();
            } catch (IOException e) {
                throw new UnexpectedSystemException(e);
            }
            return "zip";
        }
    }
}
