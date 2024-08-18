package by.kettlebell.filestorage;

import lombok.Getter;

import java.io.ByteArrayInputStream;

@Getter
public class ObjectForUpload {
    private String fullPath;
    //private String content;
    private ByteArrayInputStream inputStream;
    private Integer size;

    public ObjectForUpload(String fullPath, String content) {
        this.fullPath = fullPath;
        this.inputStream = new ByteArrayInputStream(content.getBytes());
        this.size = this.inputStream.available();
    }
}
