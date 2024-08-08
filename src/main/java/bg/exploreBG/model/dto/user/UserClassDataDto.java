package bg.exploreBG.model.dto.user;

import bg.exploreBG.model.dto.role.RoleDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserClassDataDto {
    private final Long id;
    private final String username;
    private final String imageUrl;
    private final LocalDateTime creationDate;
    private  List<RoleDto> roles;

    public UserClassDataDto(Long id, String username, String imageUrl, LocalDateTime creationDate) {
        this.id = id;
        this.username = username;
        this.imageUrl = imageUrl;
        this.creationDate = creationDate;
        this.roles = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public List<RoleDto> getRoles() {
        return roles;
    }
}
//Long id,
//String username,
//String imageUrl,
//LocalDateTime creationDate,
//List<RoleDto> roles