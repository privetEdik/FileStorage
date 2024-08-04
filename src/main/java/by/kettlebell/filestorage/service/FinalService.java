package by.kettlebell.filestorage.service;

import by.kettlebell.filestorage.dto.*;
import by.kettlebell.filestorage.exception.ApplicationException;
import by.kettlebell.filestorage.exception.ArchiveFormationException;
import by.kettlebell.filestorage.exception.Error;
import by.kettlebell.filestorage.repository.minio.FinalRepository;
import io.minio.ListObjectsArgs;
import io.minio.Result;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinalService {
    private final FinalRepository finalRepository;

    public BreadcrumbAndContents getDataForBreadcrumb(String path, Long userId) throws ApplicationException {

        BreadcrumbAndContents breadcrumbAndContents = new BreadcrumbAndContents();

        if (path.equals("")) {
            breadcrumbAndContents.setBreadcrumbs(Collections.emptyList());
        } else {
            breadcrumbAndContents.setBreadcrumbs(generateListBreadcrumb(path));
        }

        String prefix = getUserBucket(userId) + path + "/";

        List<String> listFullPathsToObjectInFolder = finalRepository.getInfo(prefix);

        List<Element> elements = new ArrayList<>();
        if (!listFullPathsToObjectInFolder.isEmpty()) {
            elements.addAll(getContentInCurrentFolder(listFullPathsToObjectInFolder, path, prefix));
        }
        System.out.println(elements);
        breadcrumbAndContents.setContentInLastFolder(elements);

        return breadcrumbAndContents;


    }

    private List<Element> getContentInCurrentFolder(List<String> listFullPathsToObjectInFolder, String path, String prefix) throws ApplicationException {

        Set<Element> set = listFullPathsToObjectInFolder.stream()
                .map(fullPath -> {

                    String pathWithoutRootAndFirstSlash = fullPath.replaceFirst(prefix, "");



                    int index = pathWithoutRootAndFirstSlash.indexOf("/");

                    String nameObjectInFolder = "";
                    Element element = new Element();
                    if (index == -1) {
                        nameObjectInFolder = pathWithoutRootAndFirstSlash;

                        element.setStatus(Status.FILE);
                    } else {
                        nameObjectInFolder = pathWithoutRootAndFirstSlash.substring(0, index);
                        element.setStatus(Status.FOLDER);
                    }

                    element.setName(nameObjectInFolder);

                    element.setPath(path + "/" + nameObjectInFolder);

                    return element;

                })
                .collect(Collectors.toSet());

        return set.stream().sorted().toList();
    }

    private List<Breadcrumb> generateListBreadcrumb(String path) {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        int index = path.lastIndexOf("/");
        String nameFolder = "";
        String pathCurrentFolder = path;
        String pathForFolder = "";

        while (index != -1) {

            nameFolder = pathCurrentFolder.substring(index + 1);

            pathForFolder = pathCurrentFolder;

            breadcrumbs.add(Breadcrumb.builder()
                    .nameFolder(nameFolder)
                    .pathToFolder(pathForFolder)
                    .build());

            pathCurrentFolder = pathCurrentFolder.substring(0, pathCurrentFolder.lastIndexOf(nameFolder) - 1);

            index = pathCurrentFolder.lastIndexOf("/");

        }

        Collections.reverse(breadcrumbs);

        return breadcrumbs;
    }

    public void updateFolder(UpdateFolder updateFolder) throws ApplicationException {

        String prefixOldName = getUserBucket(updateFolder.getUserId())
                + updateFolder.getPathCurrent()
                + "/"
                + updateFolder.getOldName()
                + "/";
        String prefixNewName = getUserBucket(updateFolder.getUserId())
                + updateFolder.getPathCurrent()
                + "/"
                + updateFolder.getNewName()
                + "/";

        List<String> objectsForDelete = new ArrayList<>();

        List<String> listContentFolder = finalRepository.getInfo(prefixOldName);

        listContentFolder
                .forEach(o -> {
                    String prefixNewObject = o.replaceFirst(prefixOldName, prefixNewName);
                    if (o.contains(".")) {

                        finalRepository.copyObject(o, prefixNewObject);

                    } else {
                        finalRepository.createEmptyFolder(prefixNewObject);
                    }
                    objectsForDelete.add(o);
                });

        finalRepository.deleteFolder(objectsForDelete);

    }

    public void delete(Element element, Long userId) throws ApplicationException {

        String prefix = getUserBucket(userId) + element.getPath();

        if (element.getStatus().name().equals(Status.FOLDER.name())) {

            List<String> content = finalRepository.getInfo(prefix + "/");

            finalRepository.deleteFolder(content);

        } else {
            finalRepository.deleteFile(prefix);
        }

    }

    public List<Element> findByFilter(String filter, Long userId) throws ApplicationException {

        String prefix = getUserBucket(userId);

        List<String> list = finalRepository.getInfo(prefix + "/");

        Set<Element> elements = new HashSet<>();

        list.stream()
                .map(s -> s.replaceFirst(prefix, ""))
                .filter(s -> s.contains(filter))
                .forEach(s -> {
                    int skip = 0;
                    int indexFilter = s.indexOf(filter, skip);
                    int indexLeftSlash;
                    int indexRightForName;
                    String pathToFolderLocation;
                    String nameObject;

                    while (indexFilter != -1) {

                        Element element = new Element();

                        String pathToFilter = s.substring(0, indexFilter);

                        indexLeftSlash = pathToFilter.lastIndexOf("/");


                        pathToFolderLocation = s.substring(0, indexLeftSlash);

                        element.setPath(pathToFolderLocation);

                        indexRightForName = s.indexOf("/", indexFilter + 1);

                        if (indexRightForName == -1) {
                            nameObject = s.substring(indexLeftSlash + 1);
                            element.setStatus(Status.FILE);
                        } else {
                            nameObject = s.substring(indexLeftSlash + 1, indexRightForName);
                            element.setStatus(Status.FOLDER);
                        }
                        element.setName(nameObject);

                        elements.add(element);
                        skip = indexFilter + 1;
                        indexFilter = s.indexOf(filter, skip);

                    }

                });

        if (elements.isEmpty()) {
            return Collections.emptyList();
        }

        return new ArrayList<>(elements.stream().sorted().toList());
    }

    public void uploadFilesAndFolders(List<MultipartFile> files, List<String> folders, Long userId) throws ApplicationException {
        finalRepository.uploadFilesAndFolders(files, folders, getUserBucket(userId));
    }

    public void findZipObject(String path, Long userId, OutputStream streamForUser) throws ApplicationException {

        if (path.contains(".")) {
            findZipOneFile(path, userId, streamForUser);

        } else {

            try {

                StringBuilder prefix = new StringBuilder(getUserBucket(userId) + path);

                prefix.append("/");

                List<String> content = finalRepository.getInfo(prefix.toString());

                ZipOutputStream zipOut = new ZipOutputStream(streamForUser);


                content.forEach(fullPathToObject -> {
                    //--------------------без слеша вначале---------------------------------
                    String pathEntry = fullPathToObject.replaceFirst(prefix.toString(), "");
                    //--------------------------------------------------------------------
                    ZipEntry entry = new ZipEntry(pathEntry);

                    try {
                        zipOut.putNextEntry(entry);
                        if (fullPathToObject.contains(".")) {//если объект не папка
                            InputStream stream = finalRepository.findInputStreamObject(fullPathToObject);
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = stream.read(buffer)) >= 0) {
                                zipOut.write(buffer, 0, length);
                            }
                            stream.close();
                        }
                        zipOut.closeEntry();
                    } catch (IOException e) {
                        log.info("Error create zip archive: {}", e.getMessage());
                        throw new ArchiveFormationException(Error.of("500","Error create zip archive"));
                    }

                });
                zipOut.close();
            } catch (IOException e) {
                log.info("Error close stream for archive: {}", e.getMessage());
                throw new ArchiveFormationException(Error.of("500","Error close stream for archive"));
            }
        }
       // return path.substring(path.lastIndexOf("/") + 1).replaceFirst("\\.", "_");
    }

    private void findZipOneFile(String path, Long userId, OutputStream streamForUser) throws ApplicationException{

        String prefix = getUserBucket(userId) + path;

        String fullPath = finalRepository.getInfo(prefix).get(0);

        ZipOutputStream zipOut = new ZipOutputStream(streamForUser);

        String nameEntry = path.substring(path.lastIndexOf("/") + 1);

        ZipEntry entry = new ZipEntry(nameEntry);

        try {
            zipOut.putNextEntry(entry);
            InputStream stream = finalRepository.findInputStreamObject(fullPath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = stream.read(buffer)) >= 0) {
                zipOut.write(buffer, 0, length);
            }
            stream.close();
            zipOut.closeEntry();
            zipOut.close();

        } catch (IOException e) {
            log.info("Error creating archive of one file: {}", e.getMessage());
            throw new ArchiveFormationException(Error.of("500","Error creating archive of one file"));

        }

    }


    private String getUserBucket(Long userId) {
        return "user-" + userId + "-files";
    }

    public void createFolder(String newFolder, String path, Long userId) throws ApplicationException{

        String prefix = getUserBucket(userId) + path + "/" + newFolder + "/";

        finalRepository.createEmptyFolder(prefix);
    }

}
