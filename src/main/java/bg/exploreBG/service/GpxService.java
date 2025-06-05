package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.gpxFile.GpxUrlDateDto;
import bg.exploreBG.model.entity.GpxEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.querybuilder.HikingTrailQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class GpxService {
    Logger logger = LoggerFactory.getLogger(GpxService.class);
    private final S3Service s3Service;
    private final GenericPersistenceService<GpxEntity> gpxPersistence;
    private final GenericPersistenceService<HikingTrailEntity> trailPersistence;
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;

    public GpxService(
            S3Service s3Service,
            GenericPersistenceService<GpxEntity> gpxPersistence,
            GenericPersistenceService<HikingTrailEntity> trailPersistence,
            HikingTrailQueryBuilder hikingTrailQueryBuilder
    ) {
        this.s3Service = s3Service;
        this.gpxPersistence = gpxPersistence;
        this.trailPersistence = trailPersistence;
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
    }

    public GpxUrlDateDto saveGpxFileIfOwner(
            Long id,
            String folder,
            MultipartFile file,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailByIdAndStatusIfOwner(id, userDetails.getUsername());
        GpxEntity gpxCurrentTrail = currentTrail.getGpxFile();

        if (gpxCurrentTrail != null) {
            throw new AppException("A GPX file already exists for this hiking trail. Please delete it before uploading a new one!",
                    HttpStatus.BAD_REQUEST);
        }

        String awsId = String.valueOf(UUID.randomUUID()).concat(".gpx");

        GpxEntity newGpx = createNewGpxEntity(file, folder, awsId);
        GpxEntity saved = this.gpxPersistence.saveEntityWithReturn(newGpx);

        currentTrail.setGpxFile(saved);
        currentTrail.setEntityStatus(SuperUserReviewStatusEnum.PENDING);
        this.trailPersistence.saveEntityWithoutReturn(currentTrail);

        return new GpxUrlDateDto(saved.getGpxUrl(), saved.getCreationDate());
    }

    public void deleteGpxFileByTrailEntity(
            HikingTrailEntity currentTrail
    ) {
        deleteGpxFile(currentTrail);
    }

    @Transactional
    public void deleteGpxFileIfOwner(
            Long id,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailByIdIfOwner(id, userDetails.getUsername());

        deleteGpxFile(currentTrail);
    }

    private void deleteGpxFile(
            HikingTrailEntity currentTrail
    ) {
        GpxEntity gpxFile = currentTrail.getGpxFile();

        if (gpxFile == null) {
            throw new AppException("Cannot delete a non-existent GPX file.", HttpStatus.BAD_REQUEST);
        }
        logger.info("Gpx file is not null - {}", gpxFile);

        String folder = gpxFile.getFolder();
        String aswId = gpxFile.getCloudId();

        boolean deleted = deleteS3GpxFileWithValidation(folder, aswId);
        logger.info("Deleted s3 - {}", deleted);
        currentTrail.setGpxFile(null);
        this.trailPersistence.saveEntityWithoutReturn(currentTrail);

        this.gpxPersistence.deleteEntityWithoutReturn(gpxFile);

    }

    private GpxEntity createNewGpxEntity(
            MultipartFile file,
            String folder,
            String awsId
    ) {
        String url = uploadS3GpxFileWithValidation(folder, awsId, file);

        GpxEntity gpx = new GpxEntity();
        gpx.setCloudId(awsId);
        gpx.setGpxUrl(url);
        gpx.setFolder(folder);
        gpx.setCreationDate(LocalDateTime.now());
        gpx.setStatus(StatusEnum.PENDING);
        return gpx;
    }

    private String uploadS3GpxFileWithValidation(
            String folder,
            String awsId,
            MultipartFile file
    ) {
        String url = this.s3Service.uploadGpxFile(folder, awsId, file);
        if (url == null) {
            throw new AppException("Invalid gpx url!", HttpStatus.BAD_REQUEST);
        }
        return url;
    }

    private boolean deleteS3GpxFileWithValidation(
            String folder,
            String awsId
    ) {
        boolean deleted = this.s3Service.deleteGpxFile(folder, awsId);

        if (!deleted) {
            throw new AppException("Failed to delete the resource due to an unexpected error on the server.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return true;
    }
}
