package by.kettlebell.filestorage.exception;

public class MinioApiException extends ApplicationException{
    public MinioApiException(Error error) {
        super(error);
    }
}
