package by.kettlebell.filestorage.dto;

import lombok.*;

import java.util.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Breadcrumb {
    private String nameFolder;
    private String pathToFolder;
}

