package by.kettlebell.filestorage.dto;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
//@PasswordMatches
public class UserDto {
//    @NotNull
//    @NotEmpty
    private String login;
//    @NotNull
//    @NotEmpty
 //   private String lastName;
//    @NotNull
//    @NotEmpty
    private String password;
//    private String matchingPassword;
//    @ValidEmail
//    @NotNull
//    @NotEmpty
//    private String email;
}
