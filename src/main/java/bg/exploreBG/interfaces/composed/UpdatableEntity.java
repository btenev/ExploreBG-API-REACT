package bg.exploreBG.interfaces.composed;

import bg.exploreBG.interfaces.base.HasEntityStatus;
import bg.exploreBG.interfaces.base.HasModificationDate;
import bg.exploreBG.interfaces.base.HasStatus;

public interface UpdatableEntity extends HasModificationDate, HasStatus, HasEntityStatus {
}
