package by.kettlebell.filestorage.repository;

import by.kettlebell.filestorage.dto.*;
import by.kettlebell.filestorage.repository.dao.FileDAO;
import by.kettlebell.filestorage.dto.entity.Bucket;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileDAO {
    private static final Integer PART_SIZE = -1;

    private static final String MAIN_BUCKET = "user-files";

    private final MinioClient minioClient;


//    @Override
//    public FileInfo upload(FileInfo fileInfo) {
//        return null;
//    }

    public boolean isBucketExists(String bucketName)  {
        try {
            return  minioClient.bucketExists(BucketExistsArgs.builder()
                            .bucket(bucketName)
                    .build());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
    public void createMainBucketUser(Long id)  {
        try {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket("user-"+id+"-files").build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileInfo upload/*FileWithFolders*/(FileInfo fileInfo)  {

        try {

           ObjectWriteResponse resp = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(fileInfo.getRootBucket())
                            .object(fileInfo.getMainBucketUser()  + "/"  + fileInfo.getNameFileInResources() )
                            .stream(fileInfo.getInputStream(), fileInfo.getSize(), PART_SIZE)
                            .build());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return fileInfo;
    }

    public void loadInBucket(Bucket object) {



        try {
            FileInputStream inputStream = new FileInputStream(object.getPathToFile());

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(object.getBucketName())
                            .object(object.getFileName())
                            .stream(inputStream,inputStream.available(), -1)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public void findObject(Bucket object) {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(object.getBucketName())
                        .object(object.getFileName())
                        .build()
        )){
//            StringBuilder builder = new StringBuilder();
            int a;

            while ((a = stream.read())!=-1){

                System.out.println((char) a);
            }

            System.out.println("11111111");
            //stream.read();
        } catch (Exception  e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteBucket(String nameBucket) {
        try {

            minioClient.removeBucket(
                    RemoveBucketArgs.builder().bucket(nameBucket).build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public FileInfo findPath(String path, Long userId) {

        // ПРОСМОТР ВСЕХ ДОСТУПНЫХ ФАЙЛОВ-ОБЪЕКТОВ В ХРАНИЛИЩЕ МИНИО
        // ПОКАЗЫВАЕТ ПУТЬ ИХ РАСПОЛОЖЕНИЯ

        try{
            //String mainBucket = "user-files";

           // mainBucketUser = "user-"+/*id*/3+"-files";

            String emptyBucketName = "folder";

            String fileName = "file-invisible.txt";

            String prefix = "user-" + userId + "-files" +path;

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

                String pathWithoutRoot = i.objectName().substring(prefix.length());
                System.out.println("pathWithoutRoot: " + pathWithoutRoot);
                listPathToObjects.addAll(List.of(pathWithoutRoot.split("/")));
                listPathToObjects.forEach(System.out::println);
                listPathToObjects.add(i.objectName());

            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BreadcrumbAndContents getDataForBreadcrumb(String path, Long userId) {

        //------------------------------------------------------------------------------
        Set<Element> contentNameFoldersAndFiles = new HashSet<>();//исключает дубликаты
        //------------------------------------------------------------------------------


        BreadcrumbAndContents breadcrumbAndContents = new  BreadcrumbAndContents();// хранилище данных для вью


        String prefix = "user-" + userId + "-files" + path;

        try{

            ListObjectsArgs lArgs = ListObjectsArgs.builder()
                    .bucket(MAIN_BUCKET)
                    .prefix(prefix)
                    .recursive(true)
                    //.includeVersions(true)
                    .build();
            Iterable <Result<Item>> resp = minioClient.listObjects(lArgs);

            Iterator<Result<Item>> it = resp.iterator();
            int index;
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

                String pathWithoutRoot = i.objectName().replaceFirst(prefix+"/","");
                System.out.println("pathWithoutRoot: " + pathWithoutRoot);
                System.out.println("prefix: " + prefix);
                System.out.println("prefix length  : " + (prefix.length()));

                 index = pathWithoutRoot.indexOf("/");

                String nameObjectInFolder;
                Element element = new Element();
                 if (index==-1){
                     nameObjectInFolder = pathWithoutRoot;

                 }else {
                     nameObjectInFolder = pathWithoutRoot.substring(0,index);

                 }
                if (nameObjectInFolder.indexOf('.')!=-1){

                    element.setStatus(Status.FILE);
                } else {
                    element.setStatus(Status.FOLDER);
                }

                element.setName(nameObjectInFolder);

                System.out.println("nameObjectInFolder: " + nameObjectInFolder);
                System.out.println("pathWithoutRoot: " + pathWithoutRoot);

                contentNameFoldersAndFiles.add(element);


            }

            //------------------------------------------------------------------------------
            List<Element> Elements = new ArrayList<>(contentNameFoldersAndFiles);
            //------------------------------------------------------------------------------

            //------------------------------------------------------------------------------
            breadcrumbAndContents.setContentInLastFolder(Elements);
            //------------------------------------------------------------------------------

            System.out.println("breadcrumbAndContents.getContentInLastFolder() -> "+ breadcrumbAndContents.getContentInLastFolder());



        }catch (Exception e) {
            e.printStackTrace();
        }
        return breadcrumbAndContents;
    }

//    private String generatePrefix(String pathToFolder, Long userId){
//
//
////        String prefixForRequest =  pathToFolder.stream()
////                .collect(Collectors.joining("user-"+userId+"-files/","/",""));
//        String prefixForRequest = "user-" + userId + "-files/" + pathToFolder;
//        return prefixForRequest;
//    }

}
