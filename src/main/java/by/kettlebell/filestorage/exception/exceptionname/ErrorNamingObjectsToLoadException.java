package by.kettlebell.filestorage.exception.exceptionname;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class ErrorNamingObjectsToLoadException extends ListObjectException{
    public ErrorNamingObjectsToLoadException(List<ErrorPath> errors ) {
        super(errors);

    }
}
