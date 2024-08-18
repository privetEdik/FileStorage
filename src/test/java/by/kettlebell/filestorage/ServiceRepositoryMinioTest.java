package by.kettlebell.filestorage;

import by.kettlebell.filestorage.dto.BreadcrumbAndContents;
import by.kettlebell.filestorage.dto.Element;
import by.kettlebell.filestorage.dto.RenameObject;
import by.kettlebell.filestorage.dto.Status;
import by.kettlebell.filestorage.repository.minio.MinioRepository;
import by.kettlebell.filestorage.service.MinioService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.MinIOContainer;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
public class ServiceRepositoryMinioTest {
    private static MinioClient minioClient;
    private static MinioService minioService;
    ;
    private static String rootBucketName;

    private static final Long USER_ID = 1L;
    private static final String USER_BUCKET = "user-" + USER_ID + "-files";

    private static final MinIOContainer container = new MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z")
            .withEnv("MINIO_ROOT_USER", "minioadmin")
            .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
            .withEnv("MINIO_ROOT_BUCKET", "user-files")
            .withCommand("server /data")
            .withExposedPorts(9000);

    @BeforeAll
    public static void setUp() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        container.start();

        String minioUrl = String.format("http://%s:%d",
                container.getHost(),
                container.getFirstMappedPort());

        minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials("minioadmin", "minioadmin")
                .build();

        rootBucketName = container.getEnvMap().get("MINIO_ROOT_BUCKET");

        minioClient.makeBucket(MakeBucketArgs.builder().bucket(rootBucketName).build());
        minioService = new MinioService(new MinioRepository(rootBucketName, minioClient));

    }

    @BeforeEach
    void startData() {
        List<ObjectForUpload> objectForUploads = new ArrayList<>();
        //--------------------------------------------------------
        String firstFileContent = "Hello my friend!";
        String firstFileNameAndFullPath = "/firstLevel/firstFileContent.txt";
        objectForUploads.add(new ObjectForUpload(firstFileNameAndFullPath, firstFileContent));

        String secondFileContent = "The weather is fine!";
        String secondFileNameAndFullPath = "/firstLevel/secondLevel/secondFileContent.txt";
        objectForUploads.add(new ObjectForUpload(secondFileNameAndFullPath, secondFileContent));

        String thirdFileContent = "Tra-ta-ta!";
        String thirdFileNameAndFullPath = "/firstLevel/secondLevel/thirdLevel/thirdFileContent.txt";
        objectForUploads.add(new ObjectForUpload(thirdFileNameAndFullPath, thirdFileContent));

        String emptyFolderNameFirst = "/firstLevel/emptyFolder/";
        objectForUploads.add(new ObjectForUpload(emptyFolderNameFirst, ""));

        String emptyFolderNameSecond = "/firstLevel/secondLevel/emptyFolder/";
        objectForUploads.add(new ObjectForUpload(emptyFolderNameSecond, ""));
        //------------------------------------------------------------------------------
        String FileContent = "Somebody text for rename file";
        String fileNameAndFullPathForRename = "/folderForFileBeforeRename/fileBeforeRename.txt";
        objectForUploads.add(new ObjectForUpload(fileNameAndFullPathForRename, FileContent));
        //-------------------------------------------------------------------------

        List<SnowballObject> objects = new ArrayList<>(objectForUploads.stream()
                .map(o ->
                        new SnowballObject(
                                USER_BUCKET + o.getFullPath(),
                                o.getInputStream(),
                                o.getSize(),
                                null
                        )
                ).toList());
        try {


            minioClient.uploadSnowballObjects(
                    UploadSnowballObjectsArgs.builder().bucket(rootBucketName).objects(objects).build());

        } catch (Exception e) {
            log.info("error upload start data in tests: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testGetDataForBreadcrumb() {

        BreadcrumbAndContents result = minioService.getDataForBreadcrumb("", USER_ID);
        assertTrue(result.getBreadcrumbs().isEmpty());
        assertEquals(2, result.getContentInLastFolder().size());

        BreadcrumbAndContents result1 = minioService.getDataForBreadcrumb("/firstLevel", USER_ID);
        assertEquals("firstLevel", result1.getBreadcrumbs().get(0).getNameFolder());
        assertEquals("/firstLevel", result1.getBreadcrumbs().get(0).getPathToFolder());
        assertEquals(3, result1.getContentInLastFolder().size());

    }

    @Test
    public void testRenameFolder() {
        RenameObject renameObject = RenameObject.builder()
                .oldName("secondLevel")
                .newName("renameLevel")
                .pathCurrent("/firstLevel")
                .status(Status.FOLDER)
                .userId(1L)
                .build();

        minioService.updateObject(renameObject);

        String oldPrefix = USER_BUCKET + renameObject.getPathCurrent() + "/" + renameObject.getOldName() + "/";
        String newPrefix = USER_BUCKET + renameObject.getPathCurrent() + "/" + renameObject.getNewName() + "/";

        List<String> notFolderOldName = getInfoAboutAllObjects(oldPrefix);
        List<String> contentFolderWithNewName = getInfoAboutAllObjects(newPrefix);

        assertTrue(notFolderOldName.isEmpty());
        assertEquals(3, contentFolderWithNewName.size());

    }

    @Test
    void testRenameFile() {

        RenameObject renameObject = RenameObject.builder()
                .oldName("fileBeforeRename.txt")
                .newName("fileAfterRename.txt")
                .pathCurrent("/folderForFileBeforeRename")
                .status(Status.FILE)
                .userId(USER_ID)
                .build();

        String oldPrefix = USER_BUCKET + renameObject.getPathCurrent() + "/" + renameObject.getOldName();
        String newPrefix = USER_BUCKET + renameObject.getPathCurrent() + "/" + renameObject.getNewName();

        //-- проверка есть ли старый файл в хранилище до переименования--
        assertEquals(oldPrefix, getInfoAboutAllObjects(oldPrefix).get(0));
        //---------------------------------------------------------------

        //-- проверка есть ли новый файл в хранилище до переименования--
        assertTrue(getInfoAboutAllObjects(newPrefix).isEmpty());
        //--------------------------------------------------------------

        minioService.updateObject(renameObject);

        //-- проверка есть ли старый файл в хранилище после переименования--
        assertTrue(getInfoAboutAllObjects(oldPrefix).isEmpty());
        //---------------------------------------------------------------

        //-- проверка есть ли новый файл в хранилище после переименования--
        assertEquals(newPrefix, getInfoAboutAllObjects(newPrefix).get(0));
        //--------------------------------------------------------------


    }

    @Test
    void testDeleteFolder() {
        Element folderForDelete = Element.builder()
                .name("firstLevel")
                .path("/firstLevel")
                .status(Status.FOLDER)
                .userId(USER_ID)
                .build();
        String prefix = USER_BUCKET + folderForDelete.getPath() + "/";

        //-- проверка есть ли папка в хранилище--
        assertFalse(getInfoAboutAllObjects(prefix).isEmpty());
        //--------------------------------------------------------------

        minioService.delete(folderForDelete);

        // -- проверка есть ли папка в хранилище--
        assertTrue(getInfoAboutAllObjects(prefix).isEmpty());
        //--------------------------------------------------------------


    }

    @Test
    void testDeleteFile() {

        Element fileForDelete = Element.builder()
                .name("secondFileContent.txt")
                .path("/firstLevel/secondLevel/secondFileContent.txt")
                .status(Status.FILE)
                .userId(USER_ID)
                .build();
        String prefix = USER_BUCKET + fileForDelete.getPath();

        //-- проверка есть ли папка в хранилище до удаления--
        assertFalse(getInfoAboutAllObjects(prefix).isEmpty());
        //--------------------------------------------------------------

        minioService.delete(fileForDelete);

        // -- проверка есть ли папка в хранилище после удаления--
        assertTrue(getInfoAboutAllObjects(prefix).isEmpty());
        //--------------------------------------------------------------
    }

    @Test
    void testFindByFilter() {

        String firstFilter = "firstLevel";
        String secondFilter = "emptyFolder";
        String thirdFilter = "firstFileContent.txt";
        String fourthFilter = "o";

        Integer countFirstFilter = minioService.findByFilter(firstFilter, USER_ID).size();
        Integer countSecondFilter = minioService.findByFilter(secondFilter, USER_ID).size();
        Integer countThirdFilter = minioService.findByFilter(thirdFilter, USER_ID).size();
        Integer countFourthFilter = minioService.findByFilter(fourthFilter, USER_ID).size();

        assertEquals(1, countFirstFilter);
        assertEquals(2, countSecondFilter);
        assertEquals(1, countThirdFilter);
        assertEquals(8, countFourthFilter);
    }

    @Test
    void testUploadFilesAndFolders() {

        String firstFileName = "111.txt";
        String fullPathFirstFile = "/GoodFolder/111.txt";
        String contentType = "text/plain";
        String contentFirstFile = "Hello world!";
        MultipartFile fileFirst = new MockMultipartFile(
                fullPathFirstFile,
                firstFileName,
                contentType,
                contentFirstFile.getBytes()
        );

        String secondFileName = "222.txt";
        String fullPathSecondFile = "/GoodFolder/directory/111.txt";
        String contentSecondFile = "How do you do!";
        MultipartFile fileSecond = new MockMultipartFile(
                fullPathSecondFile,
                secondFileName,
                contentType,
                contentSecondFile.getBytes()
        );


        String fullPathFirstFolder = "/GoodFolder/emptyFolder";
        String fullPathSecondFolder = "/GoodFolder/Folder/emptyFolder";

        List<MultipartFile> fileList = List.of(fileFirst, fileSecond);
        List<String> folders = List.of(fullPathFirstFolder, fullPathSecondFolder);

        minioService.uploadFilesAndFolders(fileList, folders, USER_ID);

        String prefix = USER_BUCKET + "/GoodFolder/";

        assertEquals(4, getInfoAboutAllObjects(prefix).size());

    }

    @Test
    void testFindZipFolder() {

        String pathToFolderForDownload = "/firstLevel";
        String generalFileContents = "Hello my friend!The weather is fine!Tra-ta-ta!";
        StringBuilder result = new StringBuilder();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        minioService.findZipObject(pathToFolderForDownload, USER_ID, stream);

        InputStream inputStream = new ByteArrayInputStream(stream.toByteArray());

        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {

                if (entry.getName().contains(".")) {
                    StringBuilder stringBuilder = new StringBuilder();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        stringBuilder.append(new String(buffer, 0, len));
                    }
                    result.append(stringBuilder);
                }
                zipInputStream.closeEntry();
            }

        } catch (IOException e) {
            log.info("error testFindZipObject(): {}", e.getMessage());
            throw new RuntimeException(e);
        }

        assertEquals(generalFileContents, result.toString());

    }

    @Test
    void testFindZipFile() {

        String pathToFolderForDownload = "/firstLevel/secondLevel/secondFileContent.txt";
        String generalFileContents = "The weather is fine!";

        StringBuilder stringBuilder = new StringBuilder();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        minioService.findZipObject(pathToFolderForDownload, USER_ID, stream);

        InputStream inputStream = new ByteArrayInputStream(stream.toByteArray());

        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {

                if (entry.getName().contains(".")) {

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        stringBuilder.append(new String(buffer, 0, len));
                    }
                }
                zipInputStream.closeEntry();
            }

        } catch (IOException e) {
            log.info("error testFindZipObject(): {}", e.getMessage());
            throw new RuntimeException(e);
        }

        assertEquals(generalFileContents, stringBuilder.toString());

    }

    @Test
    void testCreateFolder() {
        String newEmptyFolder = "newEmptyDirectory";
        String currentPath = "/firstLevel";

        //была ли такая папка до создания
        assertTrue(getInfoAboutAllObjects(USER_BUCKET + currentPath + "/" + newEmptyFolder + "/").isEmpty());
        //------------------------------

        minioService.createFolder(newEmptyFolder, currentPath, USER_ID);

        //проверка наличия папки после создания
        assertFalse(getInfoAboutAllObjects(USER_BUCKET + currentPath + "/" + newEmptyFolder + "/").isEmpty());
        //------------------------------

    }

    @AfterAll
    public static void tearDown() {
        container.stop();
    }

    @AfterEach
    void clearData() {
        List<String> allObjects = getInfoAboutAllObjects(USER_BUCKET + "/");

        try {

            List<DeleteObject> allObjectForDelete = allObjects.stream().map(DeleteObject::new).toList();

            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(rootBucketName)
                            .objects(allObjectForDelete)
                            .build()

            );

            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.info("Error in deleting object {}; {}", error.objectName(), error.message());
            }


        } catch (Exception e) {
            log.info("Error delete folder: {}", e.getMessage());
            throw new RuntimeException(e);

        }

    }

    private List<String> getInfoAboutAllObjects(String prefix) {
        List<String> infoAllObjects = new ArrayList<>();

        ListObjectsArgs lArgs = ListObjectsArgs.builder()
                .bucket(rootBucketName)
                .prefix(prefix)
                .recursive(true)
                .build();
        Iterable<Result<Item>> resp = minioClient.listObjects(lArgs);

        try {

            for (Result<Item> itemResult : resp) {
                Item i = itemResult.get();
                infoAllObjects.add(i.objectName());

            }
        } catch (Exception e) {
            log.info("error test: {}, {}", e.getClass(), e.getMessage());
            throw new RuntimeException(e);
        }

        return infoAllObjects;
    }
}