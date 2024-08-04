package by.kettlebell.filestorage.controller;

import by.kettlebell.filestorage.dto.entity.User;
import by.kettlebell.filestorage.errors.UserAlreadyExistsException;
import by.kettlebell.filestorage.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "auth/reg";
    }

    @PostMapping("/registration")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "auth/reg";
        }

        try {
            userService.save(user);
            log.info("Sign up successful for user {}", user.getUsername());
            return "redirect:/login";
        } catch (UserAlreadyExistsException exc) {
            log.info("Sign up failed for user: {}| {}", user.getUsername(),exc.getMessage());
            bindingResult.rejectValue("username", "user.alreadyExists", "User with this username already exists");
            return "auth/reg";
        }

    }

}
