package bg.exploreBG.model.entity;

import bg.exploreBG.model.enums.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hiking_trail")
public class HikingTrailEntity {

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

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "season_visited")
    @Enumerated(EnumType.STRING)
    private SeasonEnum seasonVisited;

    @Column(name = "water_available")
    @Enumerated(EnumType.STRING)
    private WaterAvailabilityEnum waterAvailable;

    @ManyToMany
    @JoinTable(
            name = "hiking_trails_available_huts",
            joinColumns = @JoinColumn(name = "hiking_trail_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "available_huts_id", referencedColumnName = "id")
    )
    private List<AccommodationEntity> availableHuts;

    @Column(name = "trail_difficulty")
    @Enumerated(EnumType.ORDINAL)
    private DifficultyLevelEnum trailDifficulty;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    @CollectionTable(
            name = "hiking_trail_activity",
            joinColumns = @JoinColumn(name = "hiking_trail_id", referencedColumnName = "id"))
    private List<SuitableForEnum> activity;

    @OneToMany
    @JoinTable(
            name = "hiking_trails_comments",
            joinColumns = @JoinColumn(name = "hiking_trail_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id")
    )
    private List<CommentEntity> comments = new ArrayList<>();

    @Column(name = "elevation_gained")
    private Integer elevationGained;

    @Column(name = "next_to")
    private String nextTo;

    @Column(name = "trail_status")
    @Enumerated(EnumType.STRING)
    private StatusEnum trailStatus;

    @OneToMany
    @JoinTable(name = "hiking_trails_destinations",
               joinColumns = @JoinColumn(name = "hiking_trail_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "destination_id", referencedColumnName = "id")
    )
    private List<DestinationEntity> destinations;

    @ManyToOne
    private UserEntity createdBy;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public StatusEnum getTrailStatus() {
        return trailStatus;
    }

    public void setTrailStatus(StatusEnum trailStatus) {
        this.trailStatus = trailStatus;
    }

    public List<DestinationEntity> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<DestinationEntity> destinations) {
        this.destinations = destinations;
    }

    public UserEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserEntity createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "HikingTrailEntity{" +
                "id=" + id +
                ", startPoint='" + startPoint + '\'' +
                ", endPoint='" + endPoint + '\'' +
                ", totalDistance=" + totalDistance +
                ", trailInfo='" + trailInfo + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
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
                ", createdBy=" + createdBy +
                '}';
    }
}
