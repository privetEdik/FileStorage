package by.kettlebell.filestorage.service;

import by.kettlebell.filestorage.dto.Breadcrumb;
import by.kettlebell.filestorage.dto.BreadcrumbAndContents;
import by.kettlebell.filestorage.repository.FileRepositoryImpl;
import by.kettlebell.filestorage.repository.dao.FileDAO;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

class FileServiceImplTest {
    @Mock
    FileDAO fileDAO;

//    @Mock
//    FileRepositoryImpl fileRepository;
    @Test
    void getDataForBreadcrumb() {
        final MinioClient minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin")
                .build();
        final FileRepositoryImpl repository = new FileRepositoryImpl(minioClient);

       /* FileInfo info1 = FileInfo.builder()
                .userId(2L)
                .rootBucket("user-files")
                .mainBucketUser("user-4-files")
                .additionalFolders("zak-zak/hello/tra-ta-ta")
                .nameFileInResources("")
                .inputStream(InputStream.nullInputStream())
                .size(0L)
                .build();

        FileInfo info2 = FileInfo.builder()
                .userId(2L)
                .rootBucket("user-files")
                .mainBucketUser("user-2-files")
                .additionalFolders("zak-zak/tra-ta-ta")
                .nameFileInResources("")
                .inputStream(InputStream.nullInputStream())
                .size(0L)
                .build();

        FileInfo info3 = FileInfo.builder()
                .userId(2L)
                .rootBucket("user-files")
                .mainBucketUser("user-2-files")
                .additionalFolders("zak-zak/hello/tra-ta-ta/ppp")
                .nameFileInResources("")
                .inputStream(InputStream.nullInputStream())
                .size(0L)
                .build();

        FileInfo info4 = FileInfo.builder()
                .userId(2L)
                .rootBucket("user-files")
                .mainBucketUser("user-2-files")
                .additionalFolders("zak-zak/hello/big-folder")
                .nameFileInResources("")
                .inputStream(InputStream.nullInputStream())
                .size(0L)
                .build();

        FileInfo info5 = FileInfo.builder()
                .userId(2L)
                .rootBucket("user-files")
                .mainBucketUser("user-2-files")
                .additionalFolders("zak-zak/hello/111")
                .nameFileInResources("")
                .inputStream(InputStream.nullInputStream())
                .size(0L)
                .build()




        repository.upload(info1);
        repository.upload(info2);
        repository.upload(info3);
        repository.upload(info4);
*/



        FileServiceImpl fileService = new FileServiceImpl(fileDAO, repository);

        BreadcrumbAndContents content = fileService.getDataForBreadcrumb("zak-zak/hello/tra-ta-ta/ppp",2l);
        System.out.println("***********************");
        System.out.println("content-> "+content.toString());
        System.out.println("***********************");
    }

    @Test
    void generateListBreadcrumb() {
        final MinioClient minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin")
                .build();
        final FileRepositoryImpl repository = new FileRepositoryImpl(minioClient);

        FileServiceImpl fileService = new FileServiceImpl(fileDAO,repository);

        System.out.println( fileService.generateListBreadcrumb("/ssss/pppp/rrrrr/tttt").toString());



    }
}