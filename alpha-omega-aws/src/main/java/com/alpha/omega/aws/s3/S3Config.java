package com.alpha.omega.aws.s3;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.s3.S3Client;

@ConditionalOnProperty(prefix = "s3", name = "enabled", havingValue = "true", matchIfMissing = false)
@Configuration
@EnableConfigurationProperties({S3Properties.class})
public class S3Config {

    @Bean
    S3Client s3Client(S3Properties s3Properties){
        AwsCredentials credentials = AwsBasicCredentials.create(s3Properties.getAccessKey(), s3Properties.getSecretKey());
        AwsCredentialsProvider awsCredentialsProvider = StaticCredentialsProvider.create(credentials);
        return S3Client.builder()
                .region(s3Properties.getRegion())
                .httpClientBuilder(ApacheHttpClient.builder())
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Bean
    S3Helper s3Helper(S3Client s3Client){
        return S3Helper.builder()
                .s3Client(s3Client)
                .build();
    }
}
