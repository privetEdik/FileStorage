package by.kettlebell.filestorage.controller;

import by.kettlebell.filestorage.repository.FileRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class MainController {

    //private final MinioClient minioClient;
    private final FileRepositoryImpl minioService;
    @PostMapping("main")
    public String getMain() {



        return "main";
    }
}
