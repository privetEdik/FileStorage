package by.kettlebell.filestorage.controller;

import by.kettlebell.filestorage.dto.BreadcrumbAndContents;
import by.kettlebell.filestorage.dto.Element;
import by.kettlebell.filestorage.dto.Status;
import by.kettlebell.filestorage.dto.UpdateFolder;
import by.kettlebell.filestorage.exception.Error;
import by.kettlebell.filestorage.exception.exceptionname.ErrorNamingObjectsToLoadException;
import by.kettlebell.filestorage.exception.exceptionname.ErrorPath;
import by.kettlebell.filestorage.exception.exceptionname.ListObjectException;
import by.kettlebell.filestorage.exception.validation.ValidationException;
import by.kettlebell.filestorage.models.UserDetailsImpl;
import by.kettlebell.filestorage.service.FinalService;
import by.kettlebell.filestorage.validator.NameFileValidator;
import by.kettlebell.filestorage.validator.NameFolderValidator;
import by.kettlebell.filestorage.validator.PathFileValidator;
import by.kettlebell.filestorage.validator.PathFolderValidator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class FileController {

    private final FinalService finalService;
    private final PathFolderValidator pathFolderValidator;
    private final PathFileValidator pathFileValidator;
    private final NameFolderValidator nameFolderValidator;
    private final NameFileValidator nameFileValidator;


    @GetMapping("/breadcrumb")
    public String findBreadcrumbs(@RequestParam(required = false) String path,
                                  @AuthenticationPrincipal UserDetailsImpl userDetails,
                                  Model model) {

        model.addAttribute("login", userDetails.getUsername());

        if (path == null) {
            path = "";
        } else if (!path.isBlank()) {
            pathFolderValidator.isValid(path);
        }

        BreadcrumbAndContents breadcrumbAndContents = finalService.getDataForBreadcrumb(path, userDetails.getUserId());

        model.addAttribute("breadcrumb", breadcrumbAndContents.getBreadcrumbs());

        model.addAttribute("pathCurrentFolder", path);

        model.addAttribute("content", breadcrumbAndContents.getContentInLastFolder());

        model.addAttribute("updateFolder", new UpdateFolder());
        model.addAttribute("actionObject", new Element());

        return "breadcrumbs";

    }

    @PostMapping("/files")
    public String patchFolder(@ModelAttribute("updateFolder") UpdateFolder updateFolder,
                              @AuthenticationPrincipal UserDetailsImpl userDetails) {

        updateFolder.setUserId(userDetails.getUserId());

        finalService.updateFolder(updateFolder);

        return "redirect:/breadcrumb?path=" + updateFolder.getPathCurrent();
    }

    @PostMapping("/delete")
    public String deleteFolder(@RequestParam("nameElem") String name,
                               @RequestParam("pathElem") String path,
                               @RequestParam("statusElem") Status status,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Element element = new Element(name, status, path);

        finalService.delete(element, userDetails.getUserId());

        return "redirect:/breadcrumb?path=";
    }

    @GetMapping("/search")
    public String searchObject(@RequestParam("filter") String filter,
                               Model model,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        //нужна валидацыя

        List<Element> listForResponseOnSearch = finalService.findByFilter(filter, userDetails.getUserId());
        model.addAttribute("elements", listForResponseOnSearch);
        model.addAttribute("login", userDetails.getUsername());

        return "search-page";
    }

    @PostMapping("/dropzone")
    public String uploadFilesAndFolders(MultipartHttpServletRequest request,
                                        @RequestParam("folders") List<String> folders,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

       // PathFolderValidator pathFolderValidator = new PathFolderValidator();
        //PathFileValidator pathFileValidator = new PathFileValidator();

        List<ErrorPath> errorPaths = new ArrayList<>();

        List<MultipartFile> files = new ArrayList<>();

        Iterator<String> fileNames = request.getFileNames();

        List<String> pathsToFiles = new ArrayList<>();

        while (fileNames.hasNext()) {
            String pathToFile = fileNames.next();
            pathsToFiles.add(pathToFile);
            files.add(request.getFile(pathToFile));
        }


        pathsToFiles.forEach(path -> {
            try {

                pathFileValidator.isValid(path);
            } catch (ValidationException e) {
                errorPaths.add(new ErrorPath(path, e.getError().getMessage()));

            }
        });

        folders.forEach(folderPath -> {

            try {
                pathFolderValidator.isValid(folderPath);
            } catch (ValidationException e) {
                errorPaths.add(new ErrorPath(folderPath, e.getError().getMessage()));
            }
        });

        if (!errorPaths.isEmpty()) {

            throw new ErrorNamingObjectsToLoadException(errorPaths);
        }

        finalService.uploadFilesAndFolders(files, folders, userDetails.getUserId());

        return "redirect:/breadcrumb";
    }

    @GetMapping("/download")
    public void downloadFolder(@ModelAttribute("actionObject") Element actionObject,
                                BindingResult bindingResult,
                                //@RequestParam String path,
                               HttpServletResponse response,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        if (bindingResult.hasErrors()){


//            throw new ListObjectException();
//            bindingResult.getAllErrors().stream().forEach(objectError -> {
//                objectError.
//            });
        }


        if (actionObject.getStatus().equals(Status.FILE)){
            nameFileValidator.isValid(actionObject.getName());
            pathFileValidator.isValid(actionObject.getPath());
        } else if (actionObject.getStatus().equals(Status.FOLDER)){
            nameFolderValidator.isValid(actionObject.getName());
            pathFolderValidator.isValid(actionObject.getPath());
        }

       // String folderName = path.substring(path.lastIndexOf("/") + 1).replaceFirst("\\.", "_");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + actionObject.getName() + ".zip");

        finalService.findZipObject(actionObject.getPath(), userDetails.getUserId(), response.getOutputStream());
        System.out.println("deleteObject: "+actionObject);
    }

    @PostMapping("/new-folder")
    public String createFolder(@RequestParam String newFolder,
                               @RequestParam String currentPath,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) {

        nameFolderValidator.isValid(newFolder);

        if (currentPath == null) {
            currentPath = "";
        } else if (!currentPath.equals("")) {
            pathFolderValidator.isValid(currentPath);
        }

        finalService.createFolder(newFolder, currentPath, userDetails.getUserId());
        return "redirect:/breadcrumb?path=" + currentPath;
    }

}
