package by.kettlebell.filestorage.exception.exceptionname;

import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorPath implements Serializable{
    private String path;
    private String message;

}
