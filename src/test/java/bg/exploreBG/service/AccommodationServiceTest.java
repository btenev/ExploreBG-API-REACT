package bg.exploreBG.service;

import bg.exploreBG.model.dto.accommodation.AccommodationDetailsDto;
import bg.exploreBG.model.dto.CommentsDto;
import bg.exploreBG.model.dto.user.UserBasicInfo;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.AccessibilityEnum;
import bg.exploreBG.model.enums.AccommodationTypeEnum;
import bg.exploreBG.model.enums.GenderEnum;
import bg.exploreBG.model.mapper.AccommodationMapper;
import bg.exploreBG.repository.AccommodationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceTest {
    @Mock
    private AccommodationRepository mockAccommodationRepo;
    @Mock
    private AccommodationMapper mockAccommodationMapper;
    private AccommodationService toTest;

    @BeforeEach
    void setUp() {
        toTest = new AccommodationService(
                mockAccommodationRepo,
                mockAccommodationMapper
        );
    }

    @Test
    void testGetAccommodationById_Exist() {
        //arrange
        UserEntity owner = new UserEntity();
        owner.setId(1L);
        owner.setEmail("member@explore.bg");
        owner.setUsername("member");
        owner.setPassword("1,2,3,4");
        owner.setGender(GenderEnum.MALE);
        owner.setBirthdate(LocalDate.of(1983, 5, 10));
        owner.setImageUrl("https://picsum.photos/200");
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

        CommentsDto comments = new CommentsDto(21L, "Some comment", new UserBasicInfo(4L, "John", null));

        AccommodationDetailsDto accommodationDetailsDto =
                new AccommodationDetailsDto(6L,
                        "Turisticheska spalnia Botev",
                        new UserBasicInfo(1L, "member", "https://picsum.photos/200"),
                        "https://picsum.photos/200",
                        "https://botevbg.com/",
                        "https://picsum.photos/200",
                        "A place where you can have some rest and get some food",
                        22,
                        25.00,
                        true,
                        "BY_CAR",
                        "SHELTER",
                        "Karlovo, Kalofer",
                        List.of(comments)
                );


        when(mockAccommodationRepo.findById(6L))
                .thenReturn(Optional.of(accommodation));

        when(mockAccommodationMapper.accommodationEntityToAccommodationDetailsDto(accommodation))
                .thenReturn(accommodationDetailsDto);

        // act
        AccommodationDetailsDto result = toTest.getAccommodation(6L);

        // assert
        assertAll(
                () -> assertEquals(accommodationDetailsDto.id(), result.id()),
                () -> assertEquals(accommodationDetailsDto.owner().id(), result.owner().id()),
                () -> assertEquals(accommodationDetailsDto.comments().get(0).id(), result.comments().get(0).id())
        );


    }

    @Test
    void testGetAll_Exist() {

    }

}
