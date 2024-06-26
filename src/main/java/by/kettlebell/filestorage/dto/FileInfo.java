package by.kettlebell.filestorage.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.time.LocalDate;

@Builder(toBuilder = true)
@Getter
@ToString
public class FileInfo {
    private Long userId;

    private String rootBucket;

    private String mainBucketUser;

    private String additionalFolders;

    private String nameFileInResources;

    private InputStream inputStream;

    private Long size;
    //private String key;
    private LocalDate uploadDate;

    private Resource resource;
}
