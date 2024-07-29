package bg.exploreBG.model.dto.user;

import bg.exploreBG.model.dto.RoleProjection;

import java.time.LocalDateTime;
import java.util.List;

public interface UserAllProjection {
    Long getId();
    String getUsername();
    String getImageUrl();
    LocalDateTime getCreationDate();


    List<RoleProjection> getRoles();
}
