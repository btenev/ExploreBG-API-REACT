package bg.exploreBG.model.entity;

import bg.exploreBG.model.enums.AccessibilityEnum;
import bg.exploreBG.model.enums.AccommodationTypeEnum;
import bg.exploreBG.model.enums.StatusEnum;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "accommodations")
public class AccommodationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accommodation_name", nullable = false)
    private String accommodationName;

    @ManyToOne(optional = false)
    private UserEntity owner;

    //    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String site;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "accommodation_info", columnDefinition = "TEXT")
    private String accommodationInfo;

    @Column(name = "bed_capacity")
    private Integer bedCapacity;

    @Column(name = "price_per_bad")
    private Double pricePerBed;

    @Column(name = "food_available")
    private Boolean foodAvailable;

    @Enumerated(EnumType.STRING)
    private AccessibilityEnum access;

    @Enumerated(EnumType.STRING)
    private AccommodationTypeEnum type;

    @Column(name = "next_to")
    private String nextTo;

    @Column(name = "accommodation_status")
    @Enumerated(EnumType.STRING)
    private StatusEnum accommodationStatus;

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

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public Boolean getFoodAvailable() {
        return foodAvailable;
    }

    public void setFoodAvailable(Boolean foodAvailable) {
        this.foodAvailable = foodAvailable;
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

    public StatusEnum getAccommodationStatus() {
        return accommodationStatus;
    }

    public void setAccommodationStatus(StatusEnum accommodationStatus) {
        this.accommodationStatus = accommodationStatus;
    }
}
