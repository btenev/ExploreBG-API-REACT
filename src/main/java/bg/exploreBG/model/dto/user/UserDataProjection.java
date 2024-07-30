package bg.exploreBG.model.dto.user;

import bg.exploreBG.model.dto.role.RoleProjection;

import java.time.LocalDateTime;
import java.util.List;

public interface UserDataProjection {
    Long getId();
    String getUsername();
    String getImageUrl();
    LocalDateTime getCreationDate();
    List<RoleProjection> getRoles();
}
