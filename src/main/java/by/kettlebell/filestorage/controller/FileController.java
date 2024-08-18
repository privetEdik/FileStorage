package by.kettlebell.filestorage.controller;

import by.kettlebell.filestorage.dto.BreadcrumbAndContents;
import by.kettlebell.filestorage.dto.Element;
import by.kettlebell.filestorage.dto.RenameObject;
import by.kettlebell.filestorage.dto.Status;
import by.kettlebell.filestorage.exception.Error;
import by.kettlebell.filestorage.exception.exceptionname.ErrorNamingObjectsToLoadException;
import by.kettlebell.filestorage.exception.exceptionname.ErrorPath;
import by.kettlebell.filestorage.exception.validation.ElementNameIsNotInPathException;
import by.kettlebell.filestorage.exception.validation.InvalidStatusException;
import by.kettlebell.filestorage.exception.validation.ValidationException;
import by.kettlebell.filestorage.service.details.UserDetailsImpl;
import by.kettlebell.filestorage.service.MinioService;
import by.kettlebell.filestorage.validator.NameFileValidator;
import by.kettlebell.filestorage.validator.NameFolderValidator;
import by.kettlebell.filestorage.validator.PathFileValidator;
import by.kettlebell.filestorage.validator.PathFolderValidator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.*;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class FileController {

    private final MinioService minioService;
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

        BreadcrumbAndContents breadcrumbAndContents = minioService.getDataForBreadcrumb(path, userDetails.getUserId());

        model.addAttribute("breadcrumb", breadcrumbAndContents.getBreadcrumbs());

        model.addAttribute("pathCurrentFolder", path);

        model.addAttribute("content", breadcrumbAndContents.getContentInLastFolder());

        model.addAttribute("renameObject", new RenameObject());
        model.addAttribute("actionObject", new Element());

        return "breadcrumbs";

    }

    @PostMapping("/patch")
    public String patchFolder(@ModelAttribute("updateFolder") @Valid RenameObject renameObject,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new InvalidStatusException(Error.of("404", bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }

        renameObject.setUserId(userDetails.getUserId());
        if (renameObject.getStatus().equals(Status.FILE)) {
            String extension = renameObject.getOldName().substring(renameObject.getOldName().indexOf('.'));

            String newName = renameObject.getNewName().contains(".")
                    ? renameObject.getNewName().replaceFirst("\\..{1,}", extension)
                    : renameObject.getNewName().concat(extension);

            nameFileValidator.isValid(newName);
            renameObject.setNewName(newName);
            minioService.updateObject(renameObject);
        } else if (renameObject.getStatus().equals(Status.FOLDER)) {
            nameFolderValidator.isValid(renameObject.getNewName());
            minioService.updateObject(renameObject);
        }


        return "redirect:/breadcrumb?path=" + renameObject.getPathCurrent();
    }

    @PostMapping("/delete")
    public String deleteFolder(@ModelAttribute("actionObject") @Valid Element actionObject,
                               BindingResult bindingResult,
                               @RequestParam("currentPath") String currentPath,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new InvalidStatusException(Error.of("404", bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }
        actionObject.setUserId(userDetails.getUserId());
        minioService.delete(actionObject);

        return "redirect:/breadcrumb?path=" + currentPath;
    }

    @GetMapping("/search")
    public String searchObject(@RequestParam("filter") String filter,
                               Model model,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Element> listForResponseOnSearch;

        if (!filter.replaceAll("[\\w]", "")
                .replaceAll("-", "")
                .replaceFirst("\\.", "").isEmpty()) {
            listForResponseOnSearch = Collections.emptyList();
        } else {
            listForResponseOnSearch = minioService.findByFilter(filter, userDetails.getUserId());
        }


        model.addAttribute("elements", listForResponseOnSearch);
        model.addAttribute("login", userDetails.getUsername());

        return "search-page";
    }

    @PostMapping("/dropzone")
    public String uploadFilesAndFolders(MultipartHttpServletRequest request,
                                        @RequestParam("folders") List<String> folders,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

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

        minioService.uploadFilesAndFolders(files, folders, userDetails.getUserId());

        return "redirect:/breadcrumb";
    }

    @GetMapping("/download")
    public void downloadFolder(@ModelAttribute("actionObject") @Valid Element actionObject,
                               BindingResult bindingResult,
                               HttpServletResponse response,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new InvalidStatusException(Error.of("404", bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }
        if (actionObject.getStatus().equals(Status.FILE)) {
            nameFileValidator.isValid(actionObject.getName());
            pathFileValidator.isValid(actionObject.getPath());

        } else if (actionObject.getStatus().equals(Status.FOLDER)) {
            nameFolderValidator.isValid(actionObject.getName());
            pathFolderValidator.isValid(actionObject.getPath());
        }

        if (!actionObject.getPath().endsWith(actionObject.getName())) {
            throw new ElementNameIsNotInPathException(Error.of("404", "Element name is not in path"));
        }

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=" + (actionObject.getName().replaceFirst("\\.", "_")) + ".zip");

        minioService.findZipObject(actionObject.getPath(), userDetails.getUserId(), response.getOutputStream());

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

        minioService.createFolder(newFolder, currentPath, userDetails.getUserId());
        return "redirect:/breadcrumb?path=" + currentPath;
    }

}
