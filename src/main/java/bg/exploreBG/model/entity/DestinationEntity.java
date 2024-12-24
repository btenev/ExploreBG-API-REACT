package bg.exploreBG.model.entity;

import bg.exploreBG.commentableEntity.CommentableEntity;
import bg.exploreBG.likeable.LikeableEntity;
import bg.exploreBG.model.enums.DestinationTypeEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.ownableEntity.OwnableEntity;
import bg.exploreBG.reviewable.ReviewableWithImages;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "destinations")
public class DestinationEntity extends BaseEntity implements ReviewableWithImages, OwnableEntity, LikeableEntity, CommentableEntity {
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

    @Column(name = "destination_status")
    @Enumerated(EnumType.STRING)
    private SuperUserReviewStatusEnum entityStatus;

    @ManyToOne
    private UserEntity createdBy;

    @OneToOne
    private ImageEntity mainImage;

    @Column(name = "max_number_of_images")
    private int maxNumberOfImages;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    @ManyToMany
    private Set<UserEntity> likedByUsers;

    @OneToMany
    @JoinTable(
            name = "destinations_images",
            joinColumns = @JoinColumn(name = "destination_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<ImageEntity> images;

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

    @Override
    public void setSingleComment(CommentEntity comment) {
        this.comments.add(comment);
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }

    public SuperUserReviewStatusEnum getEntityStatus() {
        return entityStatus;
    }

    public void setEntityStatus(SuperUserReviewStatusEnum entityStatus) {
        this.entityStatus = entityStatus;
    }

    public UserEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserEntity createdBy) {
        this.createdBy = createdBy;
    }

    public ImageEntity getMainImage() {
        return mainImage;
    }

    public void setMainImage(ImageEntity mainImage) {
        this.mainImage = mainImage;
    }

    public int getMaxNumberOfImages() {
        return maxNumberOfImages;
    }

    public void setMaxNumberOfImages(int maxNumberOfImages) {
        this.maxNumberOfImages = maxNumberOfImages;
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

    @Override
    public String toString() {
        return "DestinationEntity{" +
                "id=" + id +
                ", destinationName='" + destinationName + '\'' +
                ", location='" + location + '\'' +
                ", destinationInfo='" + destinationInfo + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", nextTo='" + nextTo + '\'' +
                ", type=" + type +
                ", entityStatus=" + entityStatus +
                ", createdBy=" + createdBy +
                ", mainImage=" + mainImage +
                ", maxNumberOfImages=" + maxNumberOfImages +
                ", creationDate=" + creationDate +
                ", modificationDate=" + modificationDate +
                ", likedByUsers=" + likedByUsers +
                ", images=" + images +
                ", comments=" + comments +
                '}';
    }
}
