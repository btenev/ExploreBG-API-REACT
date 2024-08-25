package bg.exploreBG.utils;

import bg.exploreBG.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class FileConverterUtil {
    public static File convertMultipartFileToFile(MultipartFile file){
        File convFile;

        try {
            convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException | NullPointerException e) {
            throw new AppException("File conversion failure, because " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return convFile;
    }
}
