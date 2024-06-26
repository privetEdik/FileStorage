package by.kettlebell.filestorage.service;

import by.kettlebell.filestorage.dto.Breadcrumb;
import by.kettlebell.filestorage.dto.BreadcrumbAndContents;
import by.kettlebell.filestorage.repository.FileRepositoryImpl;
import by.kettlebell.filestorage.repository.dao.FileDAO;
import by.kettlebell.filestorage.dto.FileInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private static final String ROOT_BUCKET = "user-files";

    private final FileDAO fileDAO;
    private final FileRepositoryImpl fileRepository;
    // private final FileManager fileManager;


    @Transactional(rollbackFor = {IOException.class})
    @Override
    public FileInfo upload(MultipartFile resources, Long userId) throws IOException {

        //String key = generateKey(resources.getName());
        log.info("resources  {}", resources.toString());

        System.out.println("111111 resources.getName(): " + resources.getName());
        System.out.println("222222 resources.getOriginalFilename(): " + resources.getOriginalFilename());

        FileInfo createdFile = FileInfo.builder()
                .userId(userId)
                .rootBucket(ROOT_BUCKET)
                .mainBucketUser("user-" + userId + "-files")
                .additionalFolders(resources.getOriginalFilename())// имя самого файла
                .inputStream(resources.getInputStream())
                .nameFileInResources(resources.getOriginalFilename())

        //resources.getName() - имя объекта которое  в форме прописал в инпуте нейм
        //resources.getOriginalFilename() имя самого файла

                .size(resources.getSize())
                .build();

        createdFile = fileDAO.upload(createdFile);

        // fileManager.upload(resources.getBytes(),key);

        return createdFile;
    }

    @Override
    public FileInfo download(String pathToFile, String nameFullFile) {
        return null;
    }


    @Override
    public FileInfo getPath(FileInfo info) {
        return fileDAO.upload(info);
    }

    public BreadcrumbAndContents getDataForBreadcrumb(String path, Long userId) {

        log.info(path, userId);

        BreadcrumbAndContents breadcrumbAndContents = fileRepository.getDataForBreadcrumb(path, userId);

        log.info(breadcrumbAndContents.toString());


        breadcrumbAndContents.setBreadcrumbs(generateListBreadcrumb(path));

        return breadcrumbAndContents;
    }

    public List<Breadcrumb> generateListBreadcrumb(String path) {
        List<Breadcrumb> breadcrumb111111 = new ArrayList<>();
        int index = path.lastIndexOf("/");
        String nameFolder = "";
        String pathCurrentFolder = path;
        String pathForFolder = "";

        while(index !=-1){
            System.out.println("index: " +index);
            System.out.println("nameFolder: " +nameFolder);
            System.out.println("pathCurrentFolder: " +pathCurrentFolder);
            System.out.println("pathForFolder: " +pathForFolder);
            System.out.println("=================================");


            nameFolder = pathCurrentFolder.substring(index+1);

            System.out.println("nameFolder: " +nameFolder);

            pathForFolder = pathCurrentFolder;

            System.out.println("pathForFolder: " +pathForFolder);

            breadcrumb111111.add(Breadcrumb.builder()
                    .nameFolder(nameFolder)
                    .pathToFolder(pathForFolder)
                    .build());

            pathCurrentFolder = pathCurrentFolder.substring(0,pathCurrentFolder.lastIndexOf(nameFolder)-1);

            System.out.println("pathCurrentFolder: " +pathCurrentFolder);

            index =  pathCurrentFolder.lastIndexOf("/");
            System.out.println("index: " +index);
            System.out.println("=================================");
        }

        Collections.reverse(breadcrumb111111);

        return breadcrumb111111;
    }
}
