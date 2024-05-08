package bg.exploreBG.model.entity;

import bg.exploreBG.model.enums.AccessibilityEnum;
import bg.exploreBG.model.enums.AccommodationTypeEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "accommodations")
public class AccommodationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accommodation_name", nullable = false)
    private String accommodationName;

    @OneToOne(optional = false)
    private UserEntity owner;

    //    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String site;

    @Column(name = "pictures_url")
    private String picturesUrl;

    @Column(name = "accommodation_info")
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

    public String getPicturesUrl() {
        return picturesUrl;
    }

    public void setPicturesUrl(String picturesUrl) {
        this.picturesUrl = picturesUrl;
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
}
