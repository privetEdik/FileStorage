package by.kettlebell.filestorage.service.impl;

import by.kettlebell.filestorage.dto.entity.TaskImage;
import by.kettlebell.filestorage.exception.ImageUploadException;
import by.kettlebell.filestorage.service.props.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl {
    //private final MinioProperties minioProperties;
    private  MinioClient minioClient;

    public String upload(TaskImage image) {
        try {
//            minioClient = MinioClient.builder()
//                    .endpoint("http://localhost:9000")
//                    .credentials("minioadmin","minioadmin")
//                    .build();
            createBucket();
        } catch (Exception e) {
            throw new ImageUploadException("Image upload failed " + e.getMessage());
        }
        MultipartFile file = image.getFile();

        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new ImageUploadException("Image must have name");
        }

        String fileName = generateFileName(file);
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (Exception e) {
            throw new ImageUploadException("Image upload failed " + e.getMessage());
        }
        saveImage(inputStream, fileName);
        return fileName;
    }

    @SneakyThrows
    private void createBucket() {
        boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket("user-files")
                        .build()
        );
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket("user-files")
                    .build());
        }
    }

    private String generateFileName(MultipartFile file) {
        String extension = getExtension(file);
        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(MultipartFile file) {
        return file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
    }

    @SneakyThrows
    private void saveImage(InputStream inputStream, String fileName) {
        minioClient.putObject(PutObjectArgs.builder()
                .stream(inputStream, inputStream.available(), -1)
                .bucket("user-files")
                .object(fileName)
                .build());
    }
}
