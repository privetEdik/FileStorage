package by.kettlebell.filestorage.controller;

import by.kettlebell.filestorage.service.details.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping
public class ErrorController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @GetMapping("/error-exc")
    public String handleError(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (!model.containsAttribute("mapErrors")) {
            model.addAttribute("mapErrors", new HashMap<String, List<String>>());
        }
        model.addAttribute("login", userDetails.getUsername());

        return "error-page";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @GetMapping("/error-server")
    public String handleErrorForAppExc(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {


        if (!model.containsAttribute("exception")) {
            model.addAttribute("exception", "unknown exception: Checking for the existence of an error message");
        }

        model.addAttribute("login", userDetails.getUsername());

        return "error-page";
    }
}
