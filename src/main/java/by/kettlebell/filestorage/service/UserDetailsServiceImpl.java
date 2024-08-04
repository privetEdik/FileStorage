package by.kettlebell.filestorage.service;

import by.kettlebell.filestorage.dto.UserCreateEditDto;
import by.kettlebell.filestorage.dto.entity.User;
import by.kettlebell.filestorage.models.UserDetailsImpl;
import by.kettlebell.filestorage.repository.dao.UserDao;

import by.kettlebell.filestorage.repository.minio.FinalRepository;
import lombok.RequiredArgsConstructor;


/*import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;*/
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//@Transactional
//@Service
//@RequiredArgsConstructor
public class UserDetailsServiceImpl /*implements UserDetailsService*/ {

 /*   private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final FinalRepository finalRepository;*/


/*    public void create(UserCreateEditDto userCreateEditDto) {
        User user =  User.builder().build();

        userDao.save(User.builder()
                        .username(userCreateEditDto.getUsername())
                        .password(userCreateEditDto.getPassword())
                        //.role(Role.USER)
                .build());
    }*/



/*    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =  userDao.findByUsername(username);
        return new UserDetailsImpl(user);
    }*/
}
