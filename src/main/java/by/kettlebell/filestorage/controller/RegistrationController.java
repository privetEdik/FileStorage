package by.kettlebell.filestorage.controller;

import by.kettlebell.filestorage.dto.UserDto;
import by.kettlebell.filestorage.dto.entity.User;
import by.kettlebell.filestorage.errors.UserAlreadyExistsException;
import by.kettlebell.filestorage.exception.ExceptionBody;
import by.kettlebell.filestorage.exception.ImageUploadException;
import by.kettlebell.filestorage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

//@Slf4j
//@RestController
//@RequestMapping("/api")
//@RequiredArgsConstructor
public class RegistrationController {

//    private final UserService userService;
//
//    @GetMapping("/login")
//    public String showRegistrationForm() {
//        UserDto user = UserDto.builder()
//                .login("ssss")
//                .password("222")
//                .build();
//        User user2 = userService.registerNewUserAccount(user);
//        log.info("111111 {}" + user2.toString());
//
//        return "login";
//    }
//
//    @PostMapping("/signup")
//    public String showPost(){
//        return "hello";
//    }
//
//    @ExceptionHandler(ImageUploadException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ExceptionBody  handleImageUpload(ImageUploadException e){
//        return new ExceptionBody(e.getMessage());
//    }
}
