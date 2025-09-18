package bg.exploreBG.model.dto.accommodation.validate;

import jakarta.validation.constraints.Pattern;

public record AccommodationUpdateSiteDto(
        @Pattern(
                regexp = "^(https?://).+$",
                message = "Please provide a valid URL starting with http:// or https://"
        )
        String site
) {
}
