package by.kettlebell.filestorage.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class BreadcrumbAndContents {

    private List<Breadcrumb> breadcrumbs;
    private List<Element> contentInLastFolder;
}
