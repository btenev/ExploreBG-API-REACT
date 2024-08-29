package bg.exploreBG.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "gpxs")
public class GpxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String cloudId;

    private String folder;

    private String gpxUrl;

    public GpxEntity() {
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public String getGpxUrl() {
        return gpxUrl;
    }

    public void setGpxUrl(String gpxUrl) {
        this.gpxUrl = gpxUrl;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    @Override
    public String toString() {
        return "GpxEntity{" +
                "Id=" + Id +
                ", cloudId='" + cloudId + '\'' +
                ", folder='" + folder + '\'' +
                ", gpxUrl='" + gpxUrl + '\'' +
                '}';
    }
}
