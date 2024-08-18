package by.kettlebell.filestorage.dto.entity;

import by.kettlebell.filestorage.validator.annotation.user.UserInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
@UserInfo
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 20, message = "Username length must be in between 2 and 20 symbols")
    private String username;

    @Column(name = "password")
    @NotBlank(message = "Password is required")
    @Size(min = 3, max = 100, message = "Password length must be in between 3 and 100 symbols")
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
