package bg.exploreBG.model.entity;

import bg.exploreBG.interfaces.base.CommentableEntity;
import bg.exploreBG.interfaces.base.LikeableEntity;
import bg.exploreBG.interfaces.base.OwnableEntity;
import bg.exploreBG.interfaces.composed.ReviewableWithImages;
import bg.exploreBG.interfaces.composed.UpdatableEntity;
import bg.exploreBG.model.enums.AccessibilityEnum;
import bg.exploreBG.model.enums.AccommodationTypeEnum;
import bg.exploreBG.model.enums.FoodAvailabilityEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "accommodations")
public class AccommodationEntity extends BaseEntity implements ReviewableWithImages, UpdatableEntity, OwnableEntity, LikeableEntity, CommentableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accommodation_name", nullable = false)
    private String accommodationName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by_id", referencedColumnName = "id")
    private UserEntity createdBy;

    //    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String site;

    @Column(name = "accommodation_info", columnDefinition = "TEXT")
    private String accommodationInfo;

    @Column(name = "bed_capacity")
    private Integer bedCapacity;

    @Column(name = "price_per_bad")
    private Double pricePerBed;

    @Column(name = "available_food")
    @Enumerated(EnumType.STRING)
    private FoodAvailabilityEnum availableFood;

    @Enumerated(EnumType.STRING)
    private AccessibilityEnum access;

    @Enumerated(EnumType.STRING)
    private AccommodationTypeEnum type;

    @Column(name = "next_to")
    private String nextTo;

    @Column(name = "accommodation_status")
    @Enumerated(EnumType.STRING)
    private SuperUserReviewStatusEnum entityStatus;

    @OneToOne
    private ImageEntity mainImage;

    @OneToMany
    @JoinTable(
            name = "accommodations_images",
            joinColumns = @JoinColumn(name = "accommodation_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<ImageEntity> images;

    @ManyToMany
    private Set<UserEntity> likedByUsers;

    @Column(name = "max_number_of_images")
    private int maxNumberOfImages;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    @OneToMany
    @JoinTable(
            name = "accommodations_comments",
            joinColumns = @JoinColumn(name = "accommodation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id")
    )
    private List<CommentEntity> comments;

    public AccommodationEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccommodationName() {
        return accommodationName;
    }

    public void setAccommodationName(String accommodationName) {
        this.accommodationName = accommodationName;
    }

    public UserEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserEntity createdBy) {
        this.createdBy = createdBy;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getAccommodationInfo() {
        return accommodationInfo;
    }

    public void setAccommodationInfo(String accommodationInfo) {
        this.accommodationInfo = accommodationInfo;
    }

    public Integer getBedCapacity() {
        return bedCapacity;
    }

    public void setBedCapacity(Integer bedCapacity) {
        this.bedCapacity = bedCapacity;
    }

    public Double getPricePerBed() {
        return pricePerBed;
    }

    public void setPricePerBed(Double pricePerBed) {
        this.pricePerBed = pricePerBed;
    }


    public FoodAvailabilityEnum getAvailableFood() {
        return availableFood;
    }

    public void setAvailableFood(FoodAvailabilityEnum availableFood) {
        this.availableFood = availableFood;
    }

    public AccessibilityEnum getAccess() {
        return access;
    }

    public void setAccess(AccessibilityEnum access) {
        this.access = access;
    }

    public AccommodationTypeEnum getType() {
        return type;
    }

    public void setType(AccommodationTypeEnum type) {
        this.type = type;
    }

    public String getNextTo() {
        return nextTo;
    }

    public void setNextTo(String nextTo) {
        this.nextTo = nextTo;
    }

    public List<CommentEntity> getComments() {
        return comments;
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

    public List<ImageEntity> getImages() {
        return images;
    }

    public ImageEntity getMainImage() {
        return mainImage;
    }

    public void setMainImage(ImageEntity mainImage) {
        this.mainImage = mainImage;
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

    public void setSingleComment(CommentEntity comment) {
        this.comments.add(comment);
    }

    @Override
    public String toString() {
        return "AccommodationEntity{" +
                "id=" + id +
                ", accommodationName='" + accommodationName + '\'' +
                ", createdBy=" + createdBy +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", site='" + site + '\'' +
                ", accommodationInfo='" + accommodationInfo + '\'' +
                ", bedCapacity=" + bedCapacity +
                ", pricePerBed=" + pricePerBed +
                ", availableFood=" + availableFood +
                ", access=" + access +
                ", type=" + type +
                ", nextTo='" + nextTo + '\'' +
                ", entityStatus=" + entityStatus +
                ", mainImage=" + mainImage +
                ", images=" + images +
                ", likedByUsers=" + likedByUsers +
                ", maxNumberOfImages=" + maxNumberOfImages +
                ", creationDate=" + creationDate +
                ", modificationDate=" + modificationDate +
                ", comments=" + comments +
                '}';
    }
}
