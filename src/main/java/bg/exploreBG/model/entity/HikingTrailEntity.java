package bg.exploreBG.model.entity;

import bg.exploreBG.model.enums.DifficultyLevelEnum;
import bg.exploreBG.model.enums.SeasonEnum;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuitableForEnum;
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
    private double totalDistance;

    @Column(name = "trail_info", columnDefinition = "TEXT")
    private String trailInfo;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "season_visited")
    @Enumerated(EnumType.STRING)
    private SeasonEnum seasonVisited;

    @Column(name = "water_available")
    private Boolean waterAvailable;

    @OneToMany
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
    private double elevationGained;

    @Column(name = "next_to")
    private String nextTo;

    @Column(name = "trail_status")
    @Enumerated(EnumType.STRING)
    private StatusEnum trailStatus;

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

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
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

    public Boolean getWaterAvailable() {
        return waterAvailable;
    }

    public void setWaterAvailable(Boolean waterAvailable) {
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

    public double getElevationGained() {
        return elevationGained;
    }

    public void setElevationGained(double elevationGained) {
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
}
