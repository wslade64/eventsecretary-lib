package au.com.eventsecretary.apps;

import au.com.eventsecretary.UnexpectedSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Function;

import static au.com.eventsecretary.simm.IdentifiableUtils.random;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class DownloadFileHandler {
    private String path;
    private String returnUrl;

    private Logger logger = LoggerFactory.getLogger(DownloadFileHandler.class);

    public DownloadFileHandler(String path, String returnUrl) {
        this.path = path;
        this.returnUrl = returnUrl;
    }

    public File downloadFile(String token) {
        return new File(path, token);
    }

    public void writeName(String token, String downloadFilename) {
        try {
            FileWriter fileWriter = new FileWriter(new File(path, token + ".txt"));
            fileWriter.write(downloadFilename);
            fileWriter.close();
        } catch (IOException e) {
            throw new UnexpectedSystemException("Cant write filename file:" + token);
        }
    }

    public String readName(String token) {
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(new File(path, token + ".txt")));
            String filename = fileReader.readLine();
            fileReader.close();
            return filename;
        } catch (IOException e) {
            throw new UnexpectedSystemException("Cant read filename file:" + token);
        }
    }

    public HttpEntity<FileSystemResource> downloadRequest(String token) {
        HttpHeaders headers = new HttpHeaders();
        decorateResponse(headers, readName(token));
        return new HttpEntity<>(new FileSystemResource(downloadFile(token)), headers);
    }

    public ResponseEntity<Void> produce(String context, Function<File, String> producer) throws URISyntaxException {
        String token = random();
        File file = downloadFile(token);
        String downloadFilename = producer.apply(file);
        writeName(token, downloadFilename);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(String.format(returnUrl, context, token)));

        logger.info("Download:{}", context, responseHeaders.getLocation().toString());

        return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
    }

    public static void decorateResponse(HttpHeaders headers, String filename) {
        if (filename.endsWith("pdf")) {
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
        } else if (filename.endsWith("xlsx")) {
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        }

        String format = String.format("attachment; filename=%s", filename);
        headers.set("Content-Disposition", format);
    }
}
