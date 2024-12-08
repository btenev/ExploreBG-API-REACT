package bg.exploreBG.utils;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.enums.StatusEnum;
import org.springframework.http.HttpStatus;

public final class StatusValidationUtils {
    private StatusValidationUtils() {}

    public static void ensureEntityIsApproved(StatusEnum status, String entityName) {
        if (entityName == null || entityName.isBlank()) {
            throw new IllegalArgumentException("Entity name must not be null or empty.");
        }

        if (!StatusEnum.APPROVED.equals(status)) {
            throw new AppException(
                    String.format("%s has an invalid status: %s. Expected status: APPROVED.", entityName, status),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
