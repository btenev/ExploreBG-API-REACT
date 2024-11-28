package bg.exploreBG.model.entity;

import bg.exploreBG.model.enums.*;
import bg.exploreBG.reviewable.ReviewableWithImages;
import bg.exploreBG.updatable.UpdatableEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hiking_trail")
public class HikingTrailEntity extends BaseEntity implements ReviewableWithImages, UpdatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_point", nullable = false)
    private String startPoint;

    @Column(name = "end_point", nullable = false)
    private String endPoint;

    @Column(name = "total_distance")
    private Double totalDistance;

    @Column(name = "trail_info", columnDefinition = "TEXT")
    private String trailInfo;

    @Column(name = "season_visited")
    @Enumerated(EnumType.STRING)
    private SeasonEnum seasonVisited;

    @Column(name = "water_available")
    @Enumerated(EnumType.STRING)
    private WaterAvailabilityEnum waterAvailable;

    //TODO: discuss with Ivo if one accommodation entity can belong to more than one hiking trail
    @ManyToMany
    @JoinTable(
            name = "hiking_trails_available_huts",
            joinColumns = @JoinColumn(name = "hiking_trail_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "available_huts_id", referencedColumnName = "id")
    )
    private List<AccommodationEntity> availableHuts;

    @Column(name = "trail_difficulty")
    @Enumerated(EnumType.STRING)
    private DifficultyLevelEnum trailDifficulty;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "hiking_trail_activity",
            joinColumns = @JoinColumn(name = "hiking_trail_id", referencedColumnName = "id")
    )
    private List<SuitableForEnum> activity;
    // (cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    @OneToMany
    @JoinTable(
            name = "hiking_trails_comments",
            joinColumns = @JoinColumn(name = "hiking_trail_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id")
    )
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "hikingTrail")
    private List<HikeEntity> hikes = new ArrayList<>();

    @Column(name = "elevation_gained")
    private Integer elevationGained;

    @Column(name = "next_to")
    private String nextTo;

//    @Column(name = "details_status")
//    @Enumerated(EnumType.STRING)
//    private StatusEnum detailsStatus;

    @Column(name = "trail_status")
    @Enumerated(EnumType.STRING)
    private SuperUserReviewStatusEnum trailStatus;

    //TODO: discuss with Ivo if one destination entity can belong to more than one hiking trail
    @ManyToMany
    @JoinTable(name = "hiking_trails_destinations",
               joinColumns = @JoinColumn(name = "hiking_trail_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "destination_id", referencedColumnName = "id")
    )
    private List<DestinationEntity> destinations;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    @ManyToOne
    @JoinColumn(name = "created_by_id", referencedColumnName = "id")
    private UserEntity createdBy;

    /*@ManyToOne
    private UserEntity reviewedBy;*/

    @OneToOne
    private GpxEntity gpxFile;

    @OneToOne
    private ImageEntity mainImage;

    @OneToMany
    private List<ImageEntity> images;

    @ManyToMany
    private Set<UserEntity> likedByUsers;

    @Column(name = "max_number_of_images")
    private int maxNumberOfImages;

    public HikingTrailEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getTrailInfo() {
        return trailInfo;
    }

    public void setTrailInfo(String trailInfo) {
        this.trailInfo = trailInfo;
    }

    public SeasonEnum getSeasonVisited() {
        return seasonVisited;
    }

    public void setSeasonVisited(SeasonEnum seasonVisited) {
        this.seasonVisited = seasonVisited;
    }

    public WaterAvailabilityEnum getWaterAvailable() {
        return waterAvailable;
    }

    public void setWaterAvailable(WaterAvailabilityEnum waterAvailable) {
        this.waterAvailable = waterAvailable;
    }

    public List<AccommodationEntity> getAvailableHuts() {
        return availableHuts;
    }

    public void setAvailableHuts(List<AccommodationEntity> availableHuts) {
        this.availableHuts = availableHuts;
    }

    public DifficultyLevelEnum getTrailDifficulty() {
        return trailDifficulty;
    }

    public void setTrailDifficulty(DifficultyLevelEnum trailDifficulty) {
        this.trailDifficulty = trailDifficulty;
    }

    public List<SuitableForEnum> getActivity() {
        return activity;
    }

    public void setActivity(List<SuitableForEnum> activity) {
        this.activity = activity;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }

    public Integer getElevationGained() {
        return elevationGained;
    }

    public void setElevationGained(Integer elevationGained) {
        this.elevationGained = elevationGained;
    }

    public String getNextTo() {
        return nextTo;
    }

    public void setNextTo(String nextTo) {
        this.nextTo = nextTo;
    }

    public SuperUserReviewStatusEnum getTrailStatus() {
        return trailStatus;
    }

    public void setTrailStatus(SuperUserReviewStatusEnum trailStatus) {
        this.trailStatus = trailStatus;
    }

    public List<DestinationEntity> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<DestinationEntity> destinations) {
        this.destinations = destinations;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public UserEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserEntity createdBy) {
        this.createdBy = createdBy;
    }

    public GpxEntity getGpxFile() {
        return gpxFile;
    }

    public void setGpxFile(GpxEntity gpxFile) {
        this.gpxFile = gpxFile;
    }

    public ImageEntity getMainImage() {
        return mainImage;
    }

    public void setMainImage(ImageEntity mainImage) {
        this.mainImage = mainImage;
    }

    public List<ImageEntity> getImages() {
        return images;
    }

    public void setImages(List<ImageEntity> images) {
        this.images = images;
    }

    public Set<UserEntity> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(Set<UserEntity> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    public int getMaxNumberOfImages() {
        return maxNumberOfImages;
    }

    public void setMaxNumberOfImages(int maxNumberOfImages) {
        this.maxNumberOfImages = maxNumberOfImages;
    }

    public void setSingleComment(CommentEntity savedComment) {
        this.comments.add(savedComment);
    }

    public List<HikeEntity> getHikes() {
        return hikes;
    }

    public void setHikes(List<HikeEntity> hikes) {
        this.hikes = hikes;
    }

    @Override
    public String toString() {
        return "HikingTrailEntity{" +
                "id=" + id +
                ", startPoint='" + startPoint + '\'' +
                ", endPoint='" + endPoint + '\'' +
                ", totalDistance=" + totalDistance +
                ", trailInfo='" + trailInfo + '\'' +
                ", seasonVisited=" + seasonVisited +
                ", waterAvailable=" + waterAvailable +
                ", availableHuts=" + availableHuts +
                ", trailDifficulty=" + trailDifficulty +
                ", activity=" + activity +
                ", comments=" + comments +
                ", elevationGained=" + elevationGained +
                ", nextTo='" + nextTo + '\'' +
                ", trailStatus=" + trailStatus +
                ", destinations=" + destinations +
                ", creationDate=" + creationDate +
                ", modificationDate=" + modificationDate +
                ", createdBy=" + createdBy +
                ", gpxFile=" + gpxFile +
                ", mainImage=" + mainImage +
                ", images=" + images +
                ", likedByUsers=" + likedByUsers +
                ", maxNumberOfImages=" + maxNumberOfImages +
                ", " + super.toString() +
                '}';
    }
}
