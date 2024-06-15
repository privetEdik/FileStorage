package by.kettlebell.filestorage.repository;

import by.kettlebell.filestorage.dto.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   // User findByEmail(String email);
}
