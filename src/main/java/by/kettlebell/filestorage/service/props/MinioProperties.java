package by.kettlebell.filestorage.service.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Data
//@ConfigurationProperties(prefix = "minio")

public class MinioProperties {
    private String bucket;
    private String url;
    private String accessKey;
    private String secretKey;
}
