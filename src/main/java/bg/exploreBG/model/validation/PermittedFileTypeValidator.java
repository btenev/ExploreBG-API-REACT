package bg.exploreBG.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class PermittedFileTypeValidator implements ConstraintValidator<PermittedFileType, MultipartFile> {
    private String[] allowedTypes;
    private final Tika tika = new Tika();
    public static Logger logger = LoggerFactory.getLogger(PermittedFileTypeValidator.class);

    @Override
    public void initialize(PermittedFileType constraintAnnotation) {
        this.allowedTypes = constraintAnnotation.allowedTypes();
    }

    @Override
    public boolean isValid(
            MultipartFile multipartFile,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        if (multipartFile.isEmpty()) {
            return false;
        }

        try {
            // Use the stream to detect file type
            try (InputStream typeDetectionStream = multipartFile.getInputStream()) {
                String detectedType = this.tika.detect(typeDetectionStream);
                logger.info("File type: {}", detectedType);

                if (isAllowedType(detectedType)) {
                    return true;
                }
            }

            // Re-open the stream for GPX validation
            try (InputStream gpxStream = multipartFile.getInputStream()) {
                if (isGpxFile(gpxStream)) {
                    return true;
                }
            }

        } catch (IOException | ParserConfigurationException | SAXException e) {
            logger.error("{}", e.getMessage());
            return false;
        }
        return false;
    }

    private boolean isAllowedType(String detectType) {
        for (String allowedType : allowedTypes) {
            if (detectType.equalsIgnoreCase(allowedType)) {
                return true;
            }
        }
        return false;
    }

    private boolean isGpxFile(InputStream inputStream)
            throws ParserConfigurationException, IOException, SAXException {

        // Set up XML parsing
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document;
        try {
            document = builder.parse(inputStream);
        } catch (IOException | SAXException e) {
            logger.error("Error parsing XML: ", e);
            throw e;
        }

        // Check the Document and root element
        Element rootElement = document.getDocumentElement();
        String rootTagName = (rootElement != null) ? rootElement.getTagName() : "null";
        logger.info("Document {}", document);
        logger.info("Root element tag name: {}", rootTagName);

        // Return true if the root element tag is 'gpx'
        return "gpx".equalsIgnoreCase(rootTagName);
    }
}
