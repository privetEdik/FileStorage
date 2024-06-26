package by.kettlebell.filestorage.service;

import by.kettlebell.filestorage.dto.FileInfo;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    FileInfo upload(MultipartFile resources, Long userId) throws IOException;

    FileInfo download(String pathToFile,  String nameFullFile);

    FileInfo getPath(FileInfo info);
}
