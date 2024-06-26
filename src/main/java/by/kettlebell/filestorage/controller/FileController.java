package by.kettlebell.filestorage.controller;

import by.kettlebell.filestorage.dto.FileInfo;
import by.kettlebell.filestorage.models.UserDetailsImpl;
import by.kettlebell.filestorage.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @GetMapping("/file")
    public String getForm(){
        return "main";
    }
    @PostMapping("/upload")
    public String upload(@RequestParam("object") MultipartFile attachment/*, @AuthenticationPrincipal UserDetailsImpl userDetails*/) {
        System.out.println("attachment.getOriginalFilename() -> "+attachment.getOriginalFilename());
        try {
            Long userId = 2L;
            fileService.upload(attachment,userId);
            return "main";
        }catch (Exception e) {
            e.printStackTrace();
            return "main";
        }
    }

//    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//    public ResponseEntity<Resource> download(@PathVariable("id") Long id, String pathToFile, String nameFullFile) {
//        try{
//           // FileInfo foundFile = fileService.findById(id);
//            FileInfo foundFile = fileService.download(pathToFile,nameFullFile);
//
//            return ResponseEntity.ok()
//                    .header("Content-Disposition", "attachment; filename= "+ foundFile.getNameFileInResources())
//                    .body(foundFile.getResource());
//        }  catch (Exception e){
//            log.info("ERROR {}", e.getMessage() );
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

//    @GetMapping("/{path}")
//    public ResponseEntity<FileInfo> findPathToFile(FileInfo info){
//        try{
//            FileInfo fileInfo = fileService.getPath(info);
//            return new ResponseEntity<>(fileService.getPath(info),HttpStatus.FOUND);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info(e.getMessage());
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
}
