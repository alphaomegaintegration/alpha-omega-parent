package com.alpha.omega.aws.s3;

import com.alpha.omega.core.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.utils.IoUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Optional;


@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3Helper {
    private static final Logger logger = LoggerFactory.getLogger(S3Helper.class);

    S3Client s3Client;

    public String getBucketForEnv(String bucketName, String env){
        return new StringBuilder(bucketName).append(Constants.DASH).append(env).toString();
    }

    public Optional<HeadBucketResponse> createBucket(String bucketName) {
        try {
            S3Waiter s3Waiter = s3Client.waiter();
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(bucketRequest);
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(headBucketResponse -> logger.info("Got headBucketResponse {}",headBucketResponse.toString()));

            return waiterResponse.matched().response();

        } catch (S3Exception e) {
            logger.error("Could not create bucket ",e);
            return Optional.empty();
        }
    }

    public void listBuckets() {
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets(listBucketsRequest);
        listBucketsResponse.buckets().stream()
                .forEach(bucket -> logger.info("Got bucket {}",bucket));
    }

    public Optional<HeadObjectResponse> createFolder(String bucketName, String folderName){

      /*  PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName).key(folderName).build();

        PutObjectResponse response = s3Client.putObject(request, RequestBody.empty());
*/
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName).key(folderName).build();


        s3Client.putObject(request, RequestBody.empty());

        S3Waiter waiter = s3Client.waiter();
        HeadObjectRequest requestWait = HeadObjectRequest.builder()
                .bucket(bucketName).key(folderName).build();

        WaiterResponse<HeadObjectResponse> waiterResponse = waiter.waitUntilObjectExists(requestWait);

        //waiterResponse.matched().response().ifPresent(System.out::println);

        return waiterResponse.matched().response();
    }

    public List<S3Object> getS3Objects(String bucketName, String folderName){
        ListObjectsRequest request = ListObjectsRequest.builder()
                .bucket(bucketName)
                .prefix(folderName)
                .build();

        ListObjectsResponse response = s3Client.listObjects(request);

       return response.contents();
    }

    public Resource fromS3Object(String s3Bucket,S3Object s3Object){
        Resource resource = null;
        try {
            resource = getResourceFromS3Object(s3Bucket, s3Object);
        } catch (IOException e) {
            logger.warn("Could not extract resource from {}",s3Object.toString(),e);
            resource = new DescriptiveResource("RESOURCE COULD NOT BE LOADED!");
        }
        return resource;
    }

    public Resource getResourceFromS3Object(String bucket, S3Object s3Object) throws IOException {

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(s3Object.key())
                .build();

        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
        BufferedInputStream bis = IOUtils.buffer(new ByteArrayInputStream(response.readAllBytes()));
        Resource resource = new InputStreamResource(bis, s3Object.key());
        return resource;
    }

    /* Create a pre-signed URL to download an object in a subsequent GET request. */
    public String createPresignedGetUrl(String bucketName, String folderName) {
        try (S3Presigner presigner = S3Presigner.create()) {

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(folderName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            logger.info("Presigned URL: [{}]", presignedRequest.url().toString());
            logger.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

            return presignedRequest.url().toExternalForm();
        }
    }
    /*
    https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-s3-presign.html
     */

    /* Use the JDK HttpURLConnection (since v1.1) class to do the download. */
    public byte[] useHttpUrlConnectionToGet(String presignedUrlString) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // Capture the response body to a byte array.

        try {
            URL presignedUrl = new URL(presignedUrlString);
            HttpURLConnection connection = (HttpURLConnection) presignedUrl.openConnection();
            connection.setRequestMethod("GET");
            // Download the result of executing the request.
            try (InputStream content = connection.getInputStream()) {
                IoUtils.copy(content, byteArrayOutputStream);
            }
            logger.info("HTTP response code is " + connection.getResponseCode());

        } catch (S3Exception | IOException e) {
            logger.error(e.getMessage(), e);
        }
        return byteArrayOutputStream.toByteArray();
    }


    /*
    public void createBucket(String bucketName) {
        s3Client.createBucket(b -> b.bucket(bucketName));
        try (S3Waiter waiter = s3Client.waiter()) {
            waiter.waitUntilBucketExists(b -> b.bucket(bucketName));
        }
        logger.info("Bucket [{}] created", bucketName);
    }

     */

    public void deleteBucket(String bucketName) {
        s3Client.deleteBucket(b -> b.bucket(bucketName));
        try (S3Waiter waiter = s3Client.waiter()) {
            waiter.waitUntilBucketNotExists(b -> b.bucket(bucketName));
        }
        logger.info("Bucket [{}] deleted", bucketName);
    }

    public void deleteObject(String bucketName, String key) {
        s3Client.deleteObject(b -> b.bucket(bucketName).key(key));
        try (S3Waiter waiter = s3Client.waiter()) {
            waiter.waitUntilObjectNotExists(b -> b.bucket(bucketName).key(key));
        }
        logger.info("Object [{}] deleted", key);
    }

    public void uploadFile(String bucketName, String key, File file) {
        s3Client.putObject(b -> b.bucket(bucketName).key(key), file.toPath());
        try (S3Waiter waiter = S3Waiter.builder().client(s3Client).build() ){
            waiter.waitUntilObjectExists(w -> w.bucket(bucketName).key(key));
        }
        logger.info("File uploaded successfully");
    }


    public void uploadBytes(String bucketName, String key, byte[] bytes) {
        s3Client.putObject(builder -> builder.bucket(bucketName).key(key), RequestBody.fromBytes(bytes));
        try (S3Waiter waiter = S3Waiter.builder().client(s3Client).build() ){
            waiter.waitUntilObjectExists(w -> w.bucket(bucketName).key(key));
        }
        logger.info("File uploaded successfully");
    }

    public static File getFileForForClasspathResource(String resourcePath) {
        try {
            URL resource = S3Helper.class.getClassLoader().getResource(resourcePath);
            return Paths.get(resource.toURI()).toFile();
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
