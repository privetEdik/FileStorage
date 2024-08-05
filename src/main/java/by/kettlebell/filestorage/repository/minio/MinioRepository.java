package by.kettlebell.filestorage.repository.minio;

import by.kettlebell.filestorage.exception.ApplicationException;
import by.kettlebell.filestorage.exception.Error;
import by.kettlebell.filestorage.exception.MinioApiException;
import by.kettlebell.filestorage.exception.exceptionname.ErrorDeleteObjectsException;
import by.kettlebell.filestorage.exception.exceptionname.ErrorPath;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MinioRepository {

    @Value("${minio.bucket}")
    private String rootBucket;

    private final MinioClient minioClient;

    public List<String> getInfo(String prefix) throws ApplicationException {
        List<String> listPathToObjects = new ArrayList<>();
        try {

            ListObjectsArgs lArgs = ListObjectsArgs.builder()
                    .bucket(rootBucket)
                    .prefix(prefix)
                    .recursive(true)
                    .build();
            Iterable<Result<Item>> resp = minioClient.listObjects(lArgs);


            for (Result<Item> itemResult : resp) {
                Item i = itemResult.get();
                listPathToObjects.add(i.objectName());
            }
        } catch (Exception e) {
            log.info("Error data retrieval: {} ", e.getMessage());
            throw new MinioApiException(Error.of("500", "Error data retrieval"));
        }

        return listPathToObjects;

    }

    public void deleteFolder(List<String> prefixes) throws ApplicationException {

        List<ErrorPath> deleteErrors = new ArrayList<>();
        try {
            System.out.println(prefixes);
            List<DeleteObject> deleteObjects = prefixes.stream()
                    .map(DeleteObject::new)
                    .toList();

            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(rootBucket)
                            .objects(deleteObjects)
                            .build()

            );

            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.info("Error in deleting object {}; {}", error.objectName(), error.message());
                deleteErrors.add(new ErrorPath(error.objectName(), error.message()));
            }


        } catch (Exception e) {
            log.info("Error delete folder: {}", e.getMessage());
            throw new MinioApiException(Error.of("500", "Error delete folder"));

        }
        if (!deleteErrors.isEmpty()) {
            throw new ErrorDeleteObjectsException(deleteErrors);
        }
    }

    public void deleteFile(String prefix) throws ApplicationException {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(rootBucket)
                            .object(prefix)
                            .build()
            );
        } catch (Exception e) {
            log.info("Error delete file: {}", e.getMessage());
            throw new MinioApiException(Error.of("500", "Error delete file"));

        }
    }

    public void createEmptyFolder(String pathFull) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(rootBucket)
                            .object(pathFull)
                            .stream(InputStream.nullInputStream(), 0, -1)
                            .build());

        } catch (Exception e) {
            log.info("Error create empty folder: {}", e.getMessage());
            throw new MinioApiException(Error.of("500", "Error create empty folder"));
        }
    }

    public void copyObject(String inputPath, String outPath) {
        try {

            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(rootBucket)
                            .object(outPath)
                            .source(
                                    CopySource.builder()
                                            .bucket(rootBucket)
                                            .object(inputPath)
                                            .build()
                            )
                            .build()
            );
        } catch (Exception e) {
            log.info("Error copy object: {}", e.getMessage());
            throw new MinioApiException(Error.of("500", "Error copy object"));
        }
    }

    public void uploadFilesAndFolders(List<MultipartFile> files, List<String> folders, String userBucket) {
        try {
            List<SnowballObject> objects = new ArrayList<>();

            objects.addAll(files.stream()
                    .map(file -> {
                        try {
                            return new SnowballObject(
                                    userBucket + file.getName(),
                                    new ByteArrayInputStream(file.getBytes()),
                                    file.getSize(),
                                    null
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList());

            objects.addAll(
                    folders.stream()
                            .map(folder -> {
                                return new SnowballObject(
                                        userBucket + folder + "/",
                                        InputStream.nullInputStream(),
                                        0,
                                        null);
                            })
                            .toList()
            );

            minioClient.uploadSnowballObjects(
                    UploadSnowballObjectsArgs.builder().bucket(rootBucket).objects(objects).build());


        } catch (Exception e) {
            log.info("error loading files into storage, more details: {}", e.getMessage());
            throw new MinioApiException(Error.of("500", "error loading files into storage"));
        }
    }

    public InputStream findInputStreamObject(String fullPath) {

        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(rootBucket)
                            .object(fullPath)
                            .build());
        } catch (Exception e) {
            log.info("error find stream byte from storage: {}", e.getMessage());
            throw new MinioApiException(Error.of("500", "error find stream byte from storage"));
        }
    }

}
