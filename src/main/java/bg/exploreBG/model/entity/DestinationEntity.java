package bg.exploreBG.model.entity;

import bg.exploreBG.model.enums.DestinationTypeEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "destinations")
public class DestinationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String location;

    @Column(name = "destination_info")
    private String destinationInfo;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "next_to")
    private String nextTo;

    @Enumerated(EnumType.STRING)
    private DestinationTypeEnum type;

    public DestinationEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
