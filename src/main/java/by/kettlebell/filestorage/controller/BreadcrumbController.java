package by.kettlebell.filestorage.controller;

import by.kettlebell.filestorage.dto.Breadcrumb;
import by.kettlebell.filestorage.dto.BreadcrumbAndContents;
import by.kettlebell.filestorage.models.UserDetailsImpl;
import by.kettlebell.filestorage.service.FileServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class BreadcrumbController {

    private final FileServiceImpl fileService;
    @GetMapping("/breadcrumb")
    public String findBreadcrumbs(@RequestParam String path, /*UserDetailsImpl service,*/ Model model) {
        System.out.println("====================начало==========================================");
        log.info(path);
        try {
            if (path == null||path.equals("home")) {
                path = "";
            }

            BreadcrumbAndContents content =  fileService.getDataForBreadcrumb(path, 2L);

            String  pathCurrentFolder = "";
            if (!content.getBreadcrumbs().isEmpty()){
                pathCurrentFolder = content.getBreadcrumbs().get(content.getBreadcrumbs().size()-1).getPathToFolder();
                System.out.println("content.getContentInLastFolder(): ->"+content.getContentInLastFolder());
            }
            System.out.println("pathCurrentFolder: ->"+pathCurrentFolder);
            model.addAttribute("pathCurrentFolder" , pathCurrentFolder );
            model.addAttribute("breadcrumb", content.getBreadcrumbs());

            model.addAttribute("content",content.getContentInLastFolder());
            model.addAttribute("contentNotEmpty", content.getContentInLastFolder().get(0).getName().equals(""));
            model.addAttribute("pathLastFolder",content.getBreadcrumbs().get(content.getBreadcrumbs().size()-1));


            System.out.println("=================конец=============================================");
            return "breadcrumbs";
        } catch (Exception e) {
            e.printStackTrace();
            return "breadcrumbs";
        }
    }

}
