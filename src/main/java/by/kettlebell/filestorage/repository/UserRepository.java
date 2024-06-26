package by.kettlebell.filestorage.repository;

import by.kettlebell.filestorage.dto.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
/*import org.springframework.security.core.userdetails.UserDetails;*/
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository


public interface UserRepository extends JpaRepository<User, Long> {
    Optional <User> findByUsername(String username);


    // User findByEmail(String email);
}
