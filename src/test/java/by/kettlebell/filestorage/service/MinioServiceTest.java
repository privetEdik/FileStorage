package by.kettlebell.filestorage.service;

import by.kettlebell.filestorage.dto.FileInfo;
import by.kettlebell.filestorage.dto.FileInfoResponse;
import by.kettlebell.filestorage.repository.FileRepositoryImpl;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MinioServiceTest {

    private final static String MAIN_BUCKET = "user-files";
    private String mainBucketUser;
    private final MinioClient minioClient = MinioClient.builder()
            .endpoint("http://localhost:9000")
            .credentials(/*minioProperties.getAccessKey()*/"minioadmin", /*minioProperties.getSecretKey()*/"minioadmin")
            .build();
   private final static FileRepositoryImpl minioService = new FileRepositoryImpl(MinioClient.builder()
           .endpoint("http://localhost:9000")
           .credentials("minioadmin", "minioadmin")
           .build());

    @Test
    void createMainBucket() {
        // СОЗДАНИЕ КОРНЕВОЙ ПАПКИ USER-FILES
        Long id = 23L;

        String bucketName = "user-" + id + "-files";

        minioService.createMainBucketUser(id);

        Boolean found = minioService.isBucketExists(bucketName);

        assertTrue(found);

        minioService.deleteBucket(bucketName);

        found = minioService.isBucketExists(bucketName);

        assertFalse(found);

    }

    @Test
    void testUploadFileWithFolders() {

        //ЗАГРУЗКА ФАЙЛА В ПАПКАХ ИЗ РЕСУРСОВ АПП --> В МИНИО

        Path s;
        try {
            s = Paths.get(
                    Objects.requireNonNull(
                                    FileRepositoryImpl.class.getClassLoader().getResource("invisible.txt"))
                            .toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


        String rootFolder = "user-files";
        String mainBucketUser = "user-2-files";
        String folderEmpty = "folder-empty";
        String nameFileObjectInResources = "file.txt";
        //InputStream inputStream = InputStream.nullInputStream();
        InputStream inputStream;
        try {
             inputStream = new FileInputStream(s.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        FileInfo fileInfo = FileInfo.builder()
                .rootBucket(rootFolder)
                .mainBucketUser(mainBucketUser)
                .additionalFolders(folderEmpty)
                .nameFileInResources(nameFileObjectInResources)
                .build();


//        String emptyBucketName = "folder";
//
//        String fileName = "file-invisible.txt";

        minioService.upload(fileInfo);

//        boolean found = minioService.isBucketExists("folder");
//
//        assertFalse(found);

//        minioService.deleteBucket("user-files"+"/"+bucketName);
//
//        found = minioService.bucketExists("user-files"+"/"+bucketName);
//
//        assertFalse(found);

    }

    @Test
    void testUploadEmptyFolderToMinio() {

        //ЗАГРУЗКА(СОЗДАНИЕ) ПУСТЫХ ПАПОК  --> В МИНИО

        String rootFolder = "user-files";
        String mainBucketUser = "user-2-files";
        String folderEmpty = "directory-empty";
        String nameFileObjectInResources = "";
        InputStream inputStream = InputStream.nullInputStream();

        FileInfo fileInfo = FileInfo.builder()
                .rootBucket(rootFolder)
                .mainBucketUser(mainBucketUser)
                .additionalFolders(folderEmpty)
                .nameFileInResources(nameFileObjectInResources)
                .build();

        minioService.upload(fileInfo);

    }

    @Test
    void existsObject() {

        //ПРОВЕРКА НАЛИЧИЯ ТОЛЬКО КОРНЕВЫХ КОРЗИН

        boolean found = minioService.isBucketExists("user-files");

        assertTrue(found);
    }

    @Test
    void listAllObjects_FilesInMinioAndPathsToThem() {

        // ПРОСМОТР ВСЕХ ДОСТУПНЫХ ФАЙЛОВ-ОБЪЕКТОВ В ХРАНИЛИЩЕ МИНИО
        // ПОКАЗЫВАЕТ ПУТЬ ИХ РАСПОЛОЖЕНИЯ

        FileInfoResponse fileInfoResponse = new FileInfoResponse();

        List<String> prefixRequest = new LinkedList<>();
        prefixRequest.add("user-2-files");
        prefixRequest.add("dddd");
        prefixRequest.add("11111111");

        fileInfoResponse.getPrefix().addAll(prefixRequest);

        //prefixRequest.add()
           String prefixForRequest =  prefixRequest.stream()
                    .map(s->s)
                    .collect(Collectors.joining("/"));
        System.out.println("prefixForRequest:    " + prefixForRequest);

        try{
            //String mainBucket = "user-files";

            // mainBucketUser = "user-"+/*id*/3+"-files";

            String emptyBucketName = "folder";

            String fileName = "file-invisible.txt";

            String path= "";

            String prefix = "user-" + 2 + "-files" +path;



            ListObjectsArgs lArgs = ListObjectsArgs.builder()
                    .bucket(MAIN_BUCKET)
                    .prefix(prefix)
                    .recursive(true)
                    //.includeVersions(true)
                    .build();
            Iterable <Result<Item>> resp = minioClient.listObjects(lArgs);

            List<String> listPathToObjects = new ArrayList<>();

            Iterator<Result<Item>> it = resp.iterator();
            while (it.hasNext()) {
                Item i =it.next().get();
                System.out.println("Object: " +i.objectName() +" with size: "+i.size()
                                + " version: " + i.versionId() +" storageClass: " + i.storageClass()+" etag: "
                                + i.etag() +" useTag: "+ i.userTags()
                                + " isDeleteMarker: "+ i.isDeleteMarker()
                                + " isDir: " + i.isDir()
                                +" isLatest: "+ i.isLatest()
                                + " lastModified: " + i.lastModified()
                        // + "  owner: "+i.owner().id()
                );

               // String pathWithoutRoot = i.objectName().substring(prefix.length());
                String pathWithoutRoot = i.objectName().substring(prefix.length()+1);

                String folderContent = pathWithoutRoot.substring(0,pathWithoutRoot.indexOf("/"));

                System.out.println("folderContent: " + folderContent);
                System.out.println("pathWithoutRoot: " + pathWithoutRoot);

                fileInfoResponse.getFolderContents().add(folderContent);

                listPathToObjects.addAll(List.of(pathWithoutRoot.split("/")));

               // listPathToObjects.add(i.objectName());

            }
            System.out.println("------------------");
            listPathToObjects.forEach(System.out::println);
            System.out.println("------------------");


            System.out.println("fileInfoResponse.getFolderContents() -> "+fileInfoResponse.getFolderContents());

            System.out.println("fileInfoResponse.getPrefix() -> "+fileInfoResponse.getPrefix());

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void listBuckets() {
        //ПОКАЗЫВАЕТ СПИСОК КОРНЕВЫХ КОРЗИН
        try {
            List<Bucket> bucketList = minioClient.listBuckets();

            for(Bucket bucket: bucketList) {

                System.out.println(bucket.name()+" --- "+bucket.creationDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    @Test
    void loadInBucket() {
    }

    @Test
    void findObject() {  // find object file with data
        //ПОЛУЧЕНИЕ СОДЕРЖИМОГО КОНКРЕТНОГО ФАЙЛА-ОБЪЕКТА
        by.kettlebell.filestorage.dto.entity.Bucket bucket = by.kettlebell.filestorage.dto.entity.Bucket.builder()
                        .bucketName("user-files")
                                .fileName("user-3-files/folder/file-invisible.txt")
                                        .build();
        minioService.findObject(bucket);
    }

    @Test
    void findFolder() {
                               // НЕ РАБОТАЕТ!!!!!!!!!!!!!!!!!!!
        by.kettlebell.filestorage.dto.entity.Bucket bucket = by.kettlebell.filestorage.dto.entity.Bucket.builder()
                .bucketName("user-files")
                .fileName("user-3-files/folder")
                .build();
        minioService.findObject(bucket);
    }

    @Test
    void findRetention() {
//        try {
//            Retention retention =
//                    minioClient.getObjectRetention(
//                            GetObjectRetentionArgs.builder()
//                                    .bucket("user-files")
//                                    .object("user-3-files/folder/file-invisible.txt")
//                                    .versionId("object-version-id")
//                                    .build());
//            System.out.println("mode: " + retention.mode() + "until: " + retention.retainUntilDate());
//        } catch (Exception e){
//            e.printStackTrace();
//        }
    }

}