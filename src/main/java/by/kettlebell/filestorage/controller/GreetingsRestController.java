package by.kettlebell.filestorage.controller;

import by.kettlebell.filestorage.dto.UserCreateEditDto;
import by.kettlebell.filestorage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
/*import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;*/
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class GreetingsRestController {

    private final UserService userService;
    @GetMapping("/user")
    public String getRegistrationPage(){
        System.out.println(" IN GET METHOD REGISTRATION");
        return "registration";
    }

    @PostMapping("/user")
    public String createUser(UserCreateEditDto userDto, HttpServletRequest request){
        userService.create(userDto);
        System.out.println(request.getParameter("username"));
        System.out.println(request.getParameter("password"));
        return "/login";
    }
 /*   @GetMapping("/api/v1/greetings")
    public ResponseEntity<Map<String, String>> getGreetings() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("greeting","Hello1111, %s!".formatted(userDetails.getUsername())));
    }

    @GetMapping("/api/v2/greetings")
    public ResponseEntity<Map<String, String>> getGreetingsV2(HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) ((Authentication) request.getUserPrincipal())
                .getPrincipal();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("greeting","Hello222, %s!".formatted(userDetails.getUsername())));
    }

    @GetMapping("/api/v3/greetings")
    public ResponseEntity<Map<String, String>> getGreetingsV3(@AuthenticationPrincipal UserDetails userDetails) {


        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("greeting","Hello333, %s!".formatted(userDetails.getUsername())));
    }

    @GetMapping("/api/v5/greetings")
    public ResponseEntity<Map<String, String>> getGreetingsV5(*//*Principal*//* UsernamePasswordAuthenticationToken principal) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("greeting","Hello, %s!".formatted(principal.getName())));
    }
*/



}
