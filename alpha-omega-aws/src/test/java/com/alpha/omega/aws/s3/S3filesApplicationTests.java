package com.alpha.omega.aws.s3;

import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.services.s3.S3Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import com.alpha.omega.aws.s3.*;
import org.springframework.stereotype.Component;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;
import software.amazon.awssdk.services.s3.model.*;
import java.util.List;
import lombok.Builder;
import org.springframework.core.io.Resource;
import java.io.File;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = S3Config.class)
public class S3filesApplicationTests {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();
	private String bucketName = "aoiintegrationtestbucket";
	private String folderName= "testfolder";
	private String key ="testkey";
	
	public void setup() {
		//mockMvc = MockMvcBuilders.standaloneSetup(endpoint).build();
		//is = endpoint.getClass().getClassLoader().getResourceAsStream("Snake_River.jpg");
	}

		
	
		
	
	@Test
	public void createBucket() {
		this.contextRunner.withPropertyValues("s3.enabled=true")
        .withUserConfiguration(S3Config.class)
        .run(context -> {
        	S3Client s3Client = (S3Client)context.getBean("s3Client");
            
        	S3Helper s3Helper = (S3Helper)context.getBean("s3Helper");
           
            Optional<HeadBucketResponse> headBucketResponse = s3Helper.createBucket(bucketName);
           
        });
		
	}
	
	@Test
	public void listBuckets() {
		this.contextRunner.withPropertyValues("s3.enabled=true")
        .withUserConfiguration(S3Config.class)
        .run(context -> {
        	S3Client s3Client = (S3Client)context.getBean("s3Client");
            
        	S3Helper s3Helper = (S3Helper)context.getBean("s3Helper");
           
        	s3Helper.listBuckets();
           
        });
		
	}
	
	@Test
	public void createFolder() {
		this.contextRunner.withPropertyValues("s3.enabled=true")
        .withUserConfiguration(S3Config.class)
        .run(context -> {
        	S3Client s3Client = (S3Client)context.getBean("s3Client");
            
        	S3Helper s3Helper = (S3Helper)context.getBean("s3Helper");
           
        	Optional<HeadObjectResponse> headObjectResponse  = s3Helper.createFolder(bucketName,folderName);
        	
        });
		
	}
	
	@Test
	public void getS3Objects() {
		this.contextRunner.withPropertyValues("s3.enabled=true")
        .withUserConfiguration(S3Config.class)
        .run(context -> {
        	S3Client s3Client = (S3Client)context.getBean("s3Client");
            
        	S3Helper s3Helper = (S3Helper)context.getBean("s3Helper");
           
        	List<S3Object> listS3Objects = s3Helper.getS3Objects(bucketName,folderName);
        	
           
        });
		
	}
	
	@Test
	public void createPresignedGetUrl() {
		this.contextRunner.withPropertyValues("s3.enabled=true")
        .withUserConfiguration(S3Config.class)
        .run(context -> {
        	S3Client s3Client = (S3Client)context.getBean("s3Client");
            
        	S3Helper s3Helper = (S3Helper)context.getBean("s3Helper");
           
        	String signedURL = s3Helper.createPresignedGetUrl(bucketName,folderName);
          
        });
		
	}
	
	@Test
	public void fromS3Object() {
		this.contextRunner.withPropertyValues("s3.enabled=true")
        .withUserConfiguration(S3Config.class)
        .run(context -> {
        	S3Client s3Client = (S3Client)context.getBean("s3Client");
            
        	S3Helper s3Helper = (S3Helper)context.getBean("s3Helper");
        	List<S3Object> listS3Objects = s3Helper.getS3Objects(bucketName,folderName);
        	Resource resource = s3Helper.fromS3Object(bucketName,listS3Objects.get(0));
            
        });
		
	}
	
	@Test
	public void getResourceFromS3Object() {
		this.contextRunner.withPropertyValues("s3.enabled=true")
        .withUserConfiguration(S3Config.class)
        .run(context -> {
        	S3Client s3Client = (S3Client)context.getBean("s3Client");
            
        	S3Helper s3Helper = (S3Helper)context.getBean("s3Helper");
        	List<S3Object> listS3Objects = s3Helper.getS3Objects(bucketName,folderName);
        	Resource resource = s3Helper.getResourceFromS3Object(bucketName,listS3Objects.get(0));
           
        });
		
	}
	
	@Test
	public void useHttpUrlConnectionToGet() {
		this.contextRunner.withPropertyValues("s3.enabled=true")
        .withUserConfiguration(S3Config.class)
        .run(context -> {
        	S3Client s3Client = (S3Client)context.getBean("s3Client");
            
        	S3Helper s3Helper = (S3Helper)context.getBean("s3Helper");
        	String signedURL = s3Helper.createPresignedGetUrl(bucketName,folderName);
        	byte[] bytes = s3Helper.useHttpUrlConnectionToGet(signedURL);
            
        });
		
	}
	
	/*
	 * @Test public void deleteBucket() {
	 * this.contextRunner.withPropertyValues("s3.enabled=true")
	 * .withUserConfiguration(S3Config.class) .run(context -> { S3Client s3Client =
	 * (S3Client)context.getBean("s3Client");
	 * 
	 * S3Helper s3Helper = (S3Helper)context.getBean("s3Helper");
	 * 
	 * //s3Helper.deleteBucket(bucketName);
	 * 
	 * });
	 * 
	 * }
	 */
	
	@Test
	public void deleteObject() {
		this.contextRunner.withPropertyValues("s3.enabled=true")
        .withUserConfiguration(S3Config.class)
        .run(context -> {
        	S3Client s3Client = (S3Client)context.getBean("s3Client");
            
        	S3Helper s3Helper = (S3Helper)context.getBean("s3Helper");
        	
        	s3Helper.deleteObject(bucketName,key);
            
        });
		
	}
	
	@Test
	public void uploadFile() {
		this.contextRunner.withPropertyValues("s3.enabled=true")
        .withUserConfiguration(S3Config.class)
        .run(context -> {
        	File file = new File("dev-notes.txt");
        	S3Client s3Client = (S3Client)context.getBean("s3Client");
            
        	S3Helper s3Helper = (S3Helper)context.getBean("s3Helper");
        	
        	s3Helper.uploadFile(bucketName,key,file);
            
        });
		
	}	
}
