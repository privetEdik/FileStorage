package by.kettlebell.filestorage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class SessionController {
    @RequestMapping("/")
    public String helloAdmin(){
        return "hello admin";
    }
    @RequestMapping("/l")
    public String login(){
        return "/auth/login";
    }
    @RequestMapping("/m")
    public String main(){
        return "main";
    }
}
