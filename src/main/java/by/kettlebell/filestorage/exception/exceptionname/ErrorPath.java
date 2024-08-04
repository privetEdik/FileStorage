package by.kettlebell.filestorage.exception.exceptionname;

import lombok.*;
import org.checkerframework.checker.units.qual.N;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Component
@ToString
public class ErrorPath implements Serializable{
    private String path;
    private String message;

//    public ErrorPath(String path, String message) {
//        this.path = path;
//        this.message = message;
//    }
//    public static ErrorPath of(String path, String message) {
//        return new ErrorPath(path, message);
//    }
}
