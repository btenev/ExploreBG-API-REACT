package bg.exploreBG.model.entity;

import jakarta.persistence.*;
import org.springframework.data.geo.Point;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "hikes")
public class HikeEntity {

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
    private LocalDate hikeDate;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    private HikingTrailEntity hikingTrail;

    @Column(name = "hike_info", columnDefinition ="TEXT" )
    private String hikeInfo;

    @Column(name = "next_to")
    private String nextTo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity owner;

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

    public LocalDate getHikeDate() {
        return hikeDate;
    }

    public void setHikeDate(LocalDate hikeDate) {
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

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
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
                ", owner=" + owner +
                ", comments=" + comments +
                ", archived=" + archived +
                '}';
    }
}
