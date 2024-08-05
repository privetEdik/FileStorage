package by.kettlebell.filestorage.controller;

import by.kettlebell.filestorage.exception.ApplicationException;
import by.kettlebell.filestorage.exception.exceptionname.ErrorDeleteObjectsException;
import by.kettlebell.filestorage.exception.exceptionname.ErrorNamingObjectsToLoadException;
import by.kettlebell.filestorage.exception.exceptionname.ListObjectException;
import by.kettlebell.filestorage.validator.PathFolderValidator;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ErrorNamingObjectsToLoadException.class, ErrorDeleteObjectsException.class})
    public String handleException(ListObjectException e, RedirectAttributes redirectAttributes) {

        Map<String, List<String>> map = new HashMap<>();
        e.getErrors().forEach(error->{
                           String message = error.getMessage();
                           if (map.containsKey(message)){
                               map.get(message).add(error.getPath());
                           } else {

                               map.put(message,new ArrayList<>(List.of(error.getPath())));
                           }
                        });

        redirectAttributes.addFlashAttribute("mapErrors" ,map);

        return "redirect:/error-exc";
    }

    @ExceptionHandler(ApplicationException.class)
    public String handleExceptionAppExc(ApplicationException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("exception",e.getError().getMessage());

        return "redirect:/error-server";
    }
    @ExceptionHandler(Exception.class)
    public String handleUnknownException(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exception",e.getMessage());

        return "redirect:/error-server";
    }
}
