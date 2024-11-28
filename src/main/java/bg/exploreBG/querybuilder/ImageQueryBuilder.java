package bg.exploreBG.querybuilder;

import bg.exploreBG.repository.ImageRepository;
import org.springframework.stereotype.Component;

@Component
public class ImageQueryBuilder {
    private final ImageRepository repository;

    public ImageQueryBuilder(ImageRepository repository) {
        this.repository = repository;
    }

    public String getImagerUrlByEmail(String email) {
        return this.repository.findImageUrlByOwnerEmail(email).orElse(null);
    }
}
