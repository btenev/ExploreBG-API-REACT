package bg.exploreBG.web;

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
}
