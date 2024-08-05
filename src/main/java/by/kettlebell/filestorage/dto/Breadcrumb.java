package by.kettlebell.filestorage.dto;

import lombok.*;

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

