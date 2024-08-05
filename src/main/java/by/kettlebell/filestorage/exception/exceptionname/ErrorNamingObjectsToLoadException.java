package by.kettlebell.filestorage.exception.exceptionname;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ErrorNamingObjectsToLoadException extends ListObjectException{
    public ErrorNamingObjectsToLoadException(List<ErrorPath> errors ) {
        super(errors);

    }
}
