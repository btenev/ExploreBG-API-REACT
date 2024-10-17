package bg.exploreBG.model.entity;

import bg.exploreBG.model.enums.StatusEnum;
import jakarta.persistence.*;

@MappedSuperclass
public abstract class BaseEntity {
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    @ManyToOne
    private UserEntity reviewedBy;

    public BaseEntity() {
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public UserEntity getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(UserEntity reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "status=" + status +
                ", reviewedBy=" + (reviewedBy != null ? reviewedBy.getUsername() : "null") +
                '}';
    }
}
