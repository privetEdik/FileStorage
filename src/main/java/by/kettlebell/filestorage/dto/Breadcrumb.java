package by.kettlebell.filestorage.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Getter
@Setter
@ToString
@Builder
public class Breadcrumb {
    private String nameFolder;
    private String pathToFolder;
}

