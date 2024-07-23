package bg.exploreBG.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(
            MultipartFile file,
            String folderName,
            String cloudinaryId
            ) {
            try{
                Map<String, Object> options = new HashMap<>();
                options.put("folder", folderName);
//                options.put("overwrite", true);
                options.put("public_id", cloudinaryId);
                Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
                String publicId = (String) uploadedFile.get("public_id");
                return cloudinary.url().secure(true).publicId(publicId).generate();
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
    }
}
