package by.kettlebell.filestorage.dto.entity;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Bucket {

    private Long id;
    private String bucketName;
    private String fileName;
    private String pathToFile;
}
