package by.kettlebell.filestorage.exception.exceptionname;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class ListObjectException extends RuntimeException{
    private final List<ErrorPath> errors;

    public ListObjectException(List<ErrorPath> errors) {
        this.errors = errors;
    }
}
