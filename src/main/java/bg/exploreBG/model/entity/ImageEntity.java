package bg.exploreBG.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
public class ImageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "cloud_id")
    private String cloudId;

    private String folder;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToOne(mappedBy = "userImage")
    private UserEntity profileOwner;

//    @Column(name = "image_status")
//    @Enumerated(EnumType.STRING)
//    private StatusEnum imageStatus;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

//    @ManyToOne
//    private UserEntity reviewedBy;

    public ImageEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public UserEntity getProfileOwner() {
        return profileOwner;
    }

    public void setProfileOwner(UserEntity owner) {
        this.profileOwner = owner;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "ImageEntity{" +
                "id=" + id +
                ", imageName='" + imageName + '\'' +
                ", cloudId='" + cloudId + '\'' +
                ", folder='" + folder + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", profileOwner=" + profileOwner +
                ", creationDate=" + creationDate +
                ", " + super.toString() +
                '}';
    }
}
