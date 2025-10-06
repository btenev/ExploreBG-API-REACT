package bg.exploreBG.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record NextToDto(
   String nextTo,
   @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
   LocalDateTime lastUpdateDate
) {
}
