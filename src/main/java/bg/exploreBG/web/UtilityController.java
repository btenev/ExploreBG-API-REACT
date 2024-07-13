package bg.exploreBG.web;

import bg.exploreBG.model.dto.enums.CreateDestinationEnumDto;
import bg.exploreBG.model.dto.enums.CreateHikingTrailEnumDto;
import bg.exploreBG.model.dto.enums.RegistrationEnumDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/utility")
public class UtilityController {

    @GetMapping("/register-enums")
    public ResponseEntity<?> registrationEnums() {
        RegistrationEnumDto gender = new RegistrationEnumDto();

        return ResponseEntity.ok(gender);
    }

    @GetMapping("/create/trail-enums")
    public ResponseEntity<?> createHikingTrailEnums() {
        CreateHikingTrailEnumDto all = new CreateHikingTrailEnumDto();

        return ResponseEntity.ok(all);
    }

    @GetMapping("/create/destination-enums")
    public ResponseEntity<?> createDestinationEnums() {
        CreateDestinationEnumDto type = new CreateDestinationEnumDto();

        return ResponseEntity.ok(type);
    }
}
