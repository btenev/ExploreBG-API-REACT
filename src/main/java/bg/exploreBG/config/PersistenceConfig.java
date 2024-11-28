package bg.exploreBG.config;

import bg.exploreBG.model.entity.*;
import bg.exploreBG.repository.*;
import bg.exploreBG.service.GenericPersistenceService;
import bg.exploreBG.service.GenericPersistenceServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfig {
    @Bean
    public GenericPersistenceService<HikingTrailEntity> hikingTrailEntityPersistenceService(HikingTrailRepository hikingTrailRepository) {
        return new GenericPersistenceServiceImpl<>(hikingTrailRepository);
    }

    @Bean
    public GenericPersistenceService<HikeEntity> hikeEntityPersistenceService(HikeRepository hikeRepository) {
        return new GenericPersistenceServiceImpl<>(hikeRepository);
    }

    @Bean
    public GenericPersistenceService<GpxEntity> gpxEntityPersistenceService(GpxRepository gpxRepository) {
        return new GenericPersistenceServiceImpl<>(gpxRepository);
    }

    @Bean
    public GenericPersistenceService<UserEntity> userEntityPersistenceService(UserRepository userRepository) {
        return new GenericPersistenceServiceImpl<>(userRepository);
    }

    @Bean
    public GenericPersistenceService<ImageEntity> imageEntityPersistenceService(ImageRepository imageRepository) {
        return new GenericPersistenceServiceImpl<>(imageRepository);
    }

    @Bean
    public GenericPersistenceService<CommentEntity> commentEntityPersistenceService(CommentRepository commentRepository) {
        return new GenericPersistenceServiceImpl<>(commentRepository);
    }

    @Bean
    public GenericPersistenceService<AccommodationEntity> accommodationEntityPersistenceService(AccommodationRepository accommodationRepository) {
        return new GenericPersistenceServiceImpl<>(accommodationRepository);
    }

    @Bean
    public GenericPersistenceService<DestinationEntity> destinationEntityPersistenceService(DestinationRepository destinationRepository) {
        return new GenericPersistenceServiceImpl<>(destinationRepository);
    }
}
