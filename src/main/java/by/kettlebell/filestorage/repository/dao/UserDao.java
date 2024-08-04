package by.kettlebell.filestorage.repository.dao;

import by.kettlebell.filestorage.dto.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
/*import org.springframework.security.core.userdetails.UserDetails;*/
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository


public interface UserDao extends JpaRepository<User, Long> {
    User findByUsername(String username);
   // Optional <User> findByUsernameAndPassword(String username, String password);


    // User findByEmail(String email);
}
