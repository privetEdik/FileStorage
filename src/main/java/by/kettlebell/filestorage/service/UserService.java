package by.kettlebell.filestorage.service;

import by.kettlebell.filestorage.dto.UserCreateEditDto;
import by.kettlebell.filestorage.dto.UserDto;
import by.kettlebell.filestorage.dto.entity.Role;
import by.kettlebell.filestorage.dto.entity.User;
import by.kettlebell.filestorage.errors.UserAlreadyExistsException;
import by.kettlebell.filestorage.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;


/*import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;*/
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

//
//@Service
@Transactional/*(readOnly = true)*/
@Service
@RequiredArgsConstructor
public class UserService/* implements IUserService UserDetailsService*/ {
//    @Autowired
    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    @Override
/*    public User registerNewUserAccount(final UserDto userDto) throws UserAlreadyExistsException {

//        if (emailExists(userDto.getEmail())){
//            throw new UserAlreadyExistsException("There is an account with that email address: " + userDto.getEmail())
//        }
        final User user = new User();

        //user.getUsername(userDto.getLogin());
        user.setPassword(userDto.getPassword());
        //user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        //user.setEmail(user.getEmail());
        User user1 = userRepository.save(user);

        return user1;
    }*/

    private boolean emailExists(String email) {
        return /*userRepository.findByEmail(email)!=*/true;
    }

/*
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        new BCryptPasswordEncoder().encode(user.getPassword()),
                        Collections.singleton(user.getRole())
                ))
                .orElseThrow(()-> new UsernameNotFoundException("Failed  to  retrieve user " + username));
    }
*/

    public void create(UserCreateEditDto userCreateEditDto) {
        User user =  User.builder().build();

        userRepository.save(User.builder()
                        .username(userCreateEditDto.getUsername())
                        .password(userCreateEditDto.getPassword())
                        .role(Role.USER)
                .build());
    }
}
