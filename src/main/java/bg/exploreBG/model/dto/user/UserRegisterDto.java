package bg.exploreBG.model.dto.user;

import bg.exploreBG.model.validation.FieldMatch;
import bg.exploreBG.model.validation.UniqueUserEmail;
import bg.exploreBG.model.validation.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
@FieldMatch(
        first = "password",
        second = "confirmPassword",
        message = "Passwords do no match!"
)
public record UserRegisterDto(@NotBlank(message = "Please, enter your email!")
                              @Email(message = "User email should be valid!", regexp = "[a-z0-9._+-]+@[a-z0-9.-]+\\.[a-z]{2,4}")
                              @UniqueUserEmail(message = "User email already exist!")
                              String email,

                              @NotBlank(message = "Please, enter your username!")
                              @Size(min = 3, max = 30, message = "Username length should be between 3 and 30 characters!")
                              @Pattern(
                                      regexp = "^[A-Za-z][A-Za-z0-9_]{2,29}$",
                                      message = "Username should start with A-Z or a-z. All other characters can be letters(upper or lower case), numbers or an underscore!"
                              )
                              @UniqueUsername(message = "Username already exist!")
                              String username,

                              @NotBlank(message = "Password field cannot be empty!")
                              @Size(min = 5, max = 24, message = "Password must be between 5 and 24 characters!")
                              @Pattern(
                                      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{4,23}$",
                                      message = "Password must contain one or more digit from 0 to 9, one or more lowercase letter, one or more uppercase letter, one or more special character, no space."
                              )
                              String password,
                              String confirmPassword) {
}
