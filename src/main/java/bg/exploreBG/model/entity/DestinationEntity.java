package bg.exploreBG.model.entity;

import bg.exploreBG.model.enums.DestinationTypeEnum;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "destinations")
public class DestinationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "destination_name")
    private String destinationName;

    private String location;

    @Column(name = "destination_info", columnDefinition = "TEXT")
    private String destinationInfo;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "next_to")
    private String nextTo;

    @Enumerated(EnumType.STRING)
    private DestinationTypeEnum type;

    @OneToMany
    @JoinTable(
            name = "destinations_comments",
            joinColumns = @JoinColumn(name = "destination_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id")
    )
    private List<CommentEntity> comments;
    public DestinationEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDestinationInfo() {
        return destinationInfo;
    }

    public void setDestinationInfo(String destinationInfo) {
        this.destinationInfo = destinationInfo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNextTo() {
        return nextTo;
    }

    public void setNextTo(String nextTo) {
        this.nextTo = nextTo;
    }

    public DestinationTypeEnum getType() {
        return type;
    }

    public void setType(DestinationTypeEnum type) {
        this.type = type;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }
}
