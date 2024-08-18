package by.kettlebell.filestorage;

import by.kettlebell.filestorage.dto.entity.User;
import by.kettlebell.filestorage.exception.user.UserAlreadyExistsException;
import by.kettlebell.filestorage.repository.UserRepository;
import by.kettlebell.filestorage.service.details.UserDetailsImpl;
import by.kettlebell.filestorage.service.UserService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.testcontainers.containers.MySQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ServiceUserMySqlTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    static MySQLContainer<?> mySQLContainer =
            new MySQLContainer<>("mysql:8")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    @BeforeAll
    static void setUp() {
        mySQLContainer.start();
    }

    @Test
    void contextLoads() {
        assertThat(mySQLContainer.isRunning()).isTrue();
    }

    @AfterEach
    public void clearTable() {
        userRepository.deleteAll();
    }

    @AfterAll
    static void stopCon() {

        mySQLContainer.stop();
    }

    @Test
    void registrationUser() {

        User user = new User("John", "333");

        userService.save(user);

        User user2 = userRepository.findByUsername("John");

        assertEquals(user.getUsername(), user2.getUsername());

    }

    @Test
    void constrainViolationUser() {


        User user0 = new User("John", "333");
        User user1 = new User("John", "222");

        userService.save(user0);

        User findUser = userRepository.findByUsername("John");

        assertEquals(user0.getUsername(), findUser.getUsername());

        assertThrows(UserAlreadyExistsException.class, () -> userService.save(user1));

    }

    @Test
    void getUserSuccess() {
        User user0 = new User("John", "333");
        User userResult = userRepository.save(user0);
        UserDetailsImpl u = (UserDetailsImpl) userService.loadUserByUsername(user0.getUsername());
        assertEquals(userResult.getUsername(), u.getUsername());
        assertEquals(userResult.getId(), u.getUserId());
    }

    @Test
    void getNonExistentUser() {
        User user0 = new User("John", "333");

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(user0.getUsername()));

    }

}
