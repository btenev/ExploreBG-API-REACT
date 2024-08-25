package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.GpxCreateNewGpxDto;
import bg.exploreBG.model.dto.GpxUrlDto;
import bg.exploreBG.model.entity.GpxEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.repository.GpxRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class GpxService {

    private final GpxRepository gpxRepository;
    private final HikingTrailService hikingTrailService;
    private final S3Service s3Service;

    public GpxService(
            GpxRepository gpxRepository,
            HikingTrailService hikingTrailService,
            S3Service s3Service
    ) {
        this.gpxRepository = gpxRepository;
        this.hikingTrailService = hikingTrailService;
        this.s3Service = s3Service;
    }

    public GpxUrlDto saveGpx(
            Long id,
            GpxCreateNewGpxDto gpxCreateNewGpxDto,
            MultipartFile file,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailService.hikingTrailExistApprovedPendingUserOwner(id, userDetails.getUsername());

        GpxEntity gpxCurrentTrail = currentTrail.getGpxFile();

        if (gpxCurrentTrail != null) {
            throw new AppException("A GPX file already exists for this hiking trail. Please delete it before uploading a new one!",
                    HttpStatus.BAD_REQUEST);
        }

        String awsId = String.valueOf(UUID.randomUUID()).concat(".gpx");

        GpxEntity newGpx = createNewGpxEntity(gpxCreateNewGpxDto, file, awsId);
        GpxEntity saved = this.gpxRepository.save(newGpx);

        currentTrail.setGpxFile(saved);

        this.hikingTrailService.saveHikingTrailEntity(currentTrail);

        return new GpxUrlDto(saved.getGpxUrl());
    }

    private GpxEntity createNewGpxEntity(
            GpxCreateNewGpxDto gpxCreateNewGpxDto,
            MultipartFile file,
            String awsId
    ) {
        String name = gpxCreateNewGpxDto.name();
        String folder = gpxCreateNewGpxDto.folder();

        String url = validUpload(folder, awsId, file);

        GpxEntity gpx = new GpxEntity();
        gpx.setGpxName(name);
        gpx.setCloudId(awsId);
        gpx.setGpxUrl(url);
        gpx.setFolder(folder);

        return gpx;
    }

    private String validUpload(
            String folder,
            String awsId,
            MultipartFile file
    ) {
        String url = this.s3Service.uploadGpxFile(awsId, folder, file);
        if (url == null) {
            throw new AppException("Invalid gpx url!", HttpStatus.BAD_REQUEST);
        }
        return url;
    }
}
