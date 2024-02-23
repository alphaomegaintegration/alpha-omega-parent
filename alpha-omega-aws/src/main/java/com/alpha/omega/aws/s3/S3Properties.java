package com.alpha.omega.aws.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;

import lombok.*;
import lombok.experimental.SuperBuilder;

@ConfigurationProperties(prefix = "s3")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
@ToString
public class S3Properties {

    String accessKey;
    String secretKey;
    Boolean enabled;
    //@Builder.Default
    Region region = Region.US_EAST_1;
}
