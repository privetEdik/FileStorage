package by.kettlebell.filestorage.exception.exceptionname;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ErrorDeleteObjectsException extends ListObjectException {

    public ErrorDeleteObjectsException(List<ErrorPath> errors) {
        super(errors);
    }
}
