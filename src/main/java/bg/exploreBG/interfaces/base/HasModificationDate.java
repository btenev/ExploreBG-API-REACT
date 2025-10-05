package bg.exploreBG.interfaces.base;

import java.time.LocalDateTime;

public interface HasModificationDate {
    void setModificationDate(LocalDateTime modificationDate);
    LocalDateTime getModificationDate();
}
