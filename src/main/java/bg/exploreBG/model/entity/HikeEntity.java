package bg.exploreBG.model.entity;

import bg.exploreBG.interfaces.base.CommentableEntity;
import bg.exploreBG.interfaces.base.HasModificationDate;
import bg.exploreBG.interfaces.base.OwnableEntity;
import jakarta.persistence.*;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hikes")
public class HikeEntity implements HasModificationDate, OwnableEntity, CommentableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_point", nullable = false)
    private String startPoint;

    @Column(name = "end_point", nullable = false)
    private String endPoint;

    @Column(name = "start_point_coordinates", columnDefinition = "POINT")
    private Point startPointCoordinates;

    @Column(name = "hike_date", nullable = false)
    private LocalDateTime hikeDate;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "hiking_trail_id", referencedColumnName = "id")
    private HikingTrailEntity hikingTrail;

    @Column(name = "hike_info", columnDefinition = "TEXT")
    private String hikeInfo;

    @Column(name = "next_to")
    private String nextTo;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private UserEntity createdBy;

    @OneToMany
    @JoinTable(
            name = "hikes_comments",
            joinColumns = @JoinColumn(name = "hike_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id")
    )
    private List<CommentEntity> comments;

    private Boolean archived;

    public HikeEntity() {
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

    public Point getStartPointCoordinates() {
        return startPointCoordinates;
    }

    public void setStartPointCoordinates(Point startPointCoordinates) {
        this.startPointCoordinates = startPointCoordinates;
    }

    public LocalDateTime getHikeDate() {
        return hikeDate;
    }

    public void setHikeDate(LocalDateTime hikeDate) {
        this.hikeDate = hikeDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public HikingTrailEntity getHikingTrail() {
        return hikingTrail;
    }

    public void setHikingTrail(HikingTrailEntity hikingTrail) {
        this.hikingTrail = hikingTrail;
    }

    public String getHikeInfo() {
        return hikeInfo;
    }

    public void setHikeInfo(String hikeInfo) {
        this.hikeInfo = hikeInfo;
    }

    public String getNextTo() {
        return nextTo;
    }

    public void setNextTo(String nextTo) {
        this.nextTo = nextTo;
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

    @Override
    public UserEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserEntity createdBy) {
        this.createdBy = createdBy;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public void setSingleComment(CommentEntity savedComment) {
        this.comments.add(savedComment);
    }

    @Override
    public String toString() {
        return "HikeEntity{" +
                "id=" + id +
                ", startPoint='" + startPoint + '\'' +
                ", endPoint='" + endPoint + '\'' +
                ", startPointCoordinates=" + startPointCoordinates +
                ", hikeDate=" + hikeDate +
                ", imageUrl='" + imageUrl + '\'' +
                ", hikingTrail=" + hikingTrail +
                ", hikeInfo='" + hikeInfo + '\'' +
                ", nextTo='" + nextTo + '\'' +
                ", creationDate=" + creationDate +
                ", modificationDate=" + modificationDate +
                ", createdBy=" + createdBy +
                ", comments=" + comments +
                ", archived=" + archived +
                '}';
    }
}
