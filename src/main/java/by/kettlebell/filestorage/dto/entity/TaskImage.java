package by.kettlebell.filestorage.dto.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TaskImage {
    MultipartFile file;
}
