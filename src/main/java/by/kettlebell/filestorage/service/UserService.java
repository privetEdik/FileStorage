package by.kettlebell.filestorage.service;

import by.kettlebell.filestorage.dto.UserDto;
import by.kettlebell.filestorage.dto.entity.User;
import by.kettlebell.filestorage.errors.UserAlreadyExistsException;
import by.kettlebell.filestorage.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

//
//@Service
//@Transactional
@RequiredArgsConstructor
@Service
public class UserService implements IUserService{
//    @Autowired
    private final UserRepository userRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
    @Override
    public User registerNewUserAccount(final UserDto userDto) throws UserAlreadyExistsException {

//        if (emailExists(userDto.getEmail())){
//            throw new UserAlreadyExistsException("There is an account with that email address: " + userDto.getEmail())
//        }
        final User user = new User();

        user.setLogin(userDto.getLogin());
        user.setPassword(userDto.getPassword());
        //user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        //user.setEmail(user.getEmail());
        User user1 = userRepository.save(user);

        return user1;
    }

    private boolean emailExists(String email) {
        return /*userRepository.findByEmail(email)!=*/true;
    }
}
