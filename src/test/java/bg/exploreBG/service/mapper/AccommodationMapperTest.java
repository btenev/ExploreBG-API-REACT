package bg.exploreBG.service.mapper;

import bg.exploreBG.model.dto.accommodation.AccommodationDetailsDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.AccessibilityEnum;
import bg.exploreBG.model.enums.AccommodationTypeEnum;
import bg.exploreBG.model.enums.GenderEnum;
import bg.exploreBG.model.mapper.AccommodationMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

public class AccommodationMapperTest {

    private AccommodationMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new AccommodationMapperImpl();
    }

    @Test
    void testShouldMapAccommodationEntityToAccommodationDetailsDto() {
        ImageEntity newImage = new ImageEntity();
        newImage.setId(1L);
        newImage.setImageName("wolf");
        newImage.setImageUrl("https://picsum.photos/200");


        UserEntity owner = new UserEntity();
        owner.setId(1L);
        owner.setEmail("member@explore.bg");
        owner.setUsername("member");
        owner.setPassword("1,2,3,4");
        owner.setGender(GenderEnum.MALE);
        owner.setBirthdate(LocalDate.of(1983, 5, 10));
        owner.setUserImage(newImage);
        owner.setUserInfo("some very interesting info");

        CommentEntity comment = new CommentEntity();
        comment.setId(21L);
        comment.setMessage("Some comment!");

        AccommodationEntity accommodation = new AccommodationEntity();
        accommodation.setId(6L);
        accommodation.setAccommodationName("Turisticheska spalnia Botev");
        accommodation.setOwner(owner);
        accommodation.setPhoneNumber("+359 878 565656");
        accommodation.setSite("https://botevbg.com/");
        accommodation.setImageUrl("https://picsum.photos/200");
        accommodation.setAccommodationInfo("A place where you can have some rest and get some food");
        accommodation.setBedCapacity(22);
        accommodation.setPricePerBed(25.00);
        accommodation.setFoodAvailable(true);
        accommodation.setNextTo("Karlovo, Kalofer");
        accommodation.setAccess(AccessibilityEnum.BY_CAR);
        accommodation.setType(AccommodationTypeEnum.SHELTER);
        accommodation.setComments(List.of(comment));

        AccommodationDetailsDto actual =
                this.mapper.accommodationEntityToAccommodationDetailsDto(accommodation);

        assertAll(
                () -> assertEquals(accommodation.getId(), actual.id()),
                () -> assertEquals(accommodation.getOwner().getId(), actual.owner().id()),
                () -> assertEquals(accommodation.getComments().get(0).getId(), actual.comments().get(0).id())
        );

    }
}
