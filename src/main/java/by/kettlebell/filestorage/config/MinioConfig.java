package by.kettlebell.filestorage.config;

import by.kettlebell.filestorage.service.props.MinioProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final MinioProperties minioProperties;
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(11);
//    }
//
//    @Bean
//    public ModelAndView getModelAndView(){
//        return new ModelAndView();
//    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials(/*minioProperties.getAccessKey()*/"minioadmin", /*minioProperties.getSecretKey()*/"minioadmin")
                .build();
    }

}
