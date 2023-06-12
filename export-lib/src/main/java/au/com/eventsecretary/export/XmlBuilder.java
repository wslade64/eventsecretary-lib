package au.com.eventsecretary.export;

import au.com.eventsecretary.UnexpectedSystemException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class XmlBuilder implements FileBuilder {
    private final Object object;

    public XmlBuilder(Object object) {
        this.object = object;
    }

    @Override
    public String build(File file) {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try (FileOutputStream fos = new FileOutputStream(file)){
            xmlMapper.writeValue(fos, object);
            return "xml";
        } catch (IOException e) {
            throw new UnexpectedSystemException(e);
        }
    }
}
