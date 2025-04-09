package murkeev.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class B2S3Config {
    @Value("${b2.s3.endpoint}")
    private String endpoint;

    @Value("${b2.s3.accessKey}")
    private String accessKey;

    @Value("${b2.s3.secretKey}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        if (endpoint == null || !endpoint.startsWith("https://")) {
            throw new IllegalStateException("B2 endpoint must start with https://");
        }

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1)
                .serviceConfiguration(S3Configuration.builder().checksumValidationEnabled(false).build())
                .build();
    }
}