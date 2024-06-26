package by.kettlebell.filestorage.dto;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Getter
@Service
@Component
public class FileInfoResponse {
    private String home;
    private Map<String,String> map = new TreeMap<>();

    private List<String> prefix = new LinkedList<>();

    private  List<String> folderContents = new ArrayList<>();




}
