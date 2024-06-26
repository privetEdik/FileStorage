package by.kettlebell.filestorage.repository.dao;

import by.kettlebell.filestorage.dto.FileInfo;

public interface FileDAO {
    FileInfo upload(FileInfo fileInfo);

    boolean isBucketExists(String bucketName);
}
