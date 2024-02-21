package com.alpha.omega.batch;

import com.unhrc.bims.applicant.batch.ApplicantBatchJobFactory;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.ArrayFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import java.net.MalformedURLException;

import static com.unhrc.bims.Constants.COMMA;
import static com.unhrc.bims.applicant.batch.ApplicantBatchJobFactory.SORTED_INPUT_FILE_PATH_KEY;

@Configuration
public class BatchConfig {

    @Autowired
    Environment env;

    @Value("${applicant.batch.load.chunk.size}")
    Integer chunkSize;

    @Value("${key.generator.salt}")
    String keyGeneratorSalt;

    @Value("${key.generator.password}")
    String keyGeneratorPassword;

    @Value("${external.sort.temp.folder.path}")
    String tempFileFolder;

    @Value("${external.sort.max.memory}")
    long maxMemory;

    /*
    https://docs.spring.io/spring-batch/reference/common-patterns.html#passingDataToFutureSteps
     */
    @Bean
    public ExecutionContextPromotionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{ApplicantBatchJobFactory.PROMOTE_APPLICANT_LOAD_CHUNK_KEY,SORTED_INPUT_FILE_PATH_KEY});
        return listener;
    }

    @Bean
    LineMapper<String[]> lineMapper() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(COMMA);
        DefaultLineMapper<String[]> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(new ArrayFieldSetMapper());
        return lineMapper;
    }

    @Bean
    public FlatFileItemReader<String[]> stringArrayItemReader(
            LineMapper<String[]> lineMapper, Resource sourceResource) {
        FlatFileItemReader itemReader = new FlatFileItemReader<String[]>();
        itemReader.setLineMapper(lineMapper);
        itemReader.setResource(sourceResource);
        itemReader.setLinesToSkip(1);
        return itemReader;
    }


    @ConditionalOnProperty(prefix = "applicant.batch.load", name = "env", havingValue = "local", matchIfMissing = true)
    @Configuration
    public static class EnvLoadConfig {
        @Bean
        WritableResource errorResource(Environment env) throws MalformedURLException {
            FileUrlResource resource = new FileUrlResource(env.getProperty("applicant.batch.error.resource"));
            return resource;
        }


        @Bean
        WritableResource archiveResource(Environment env) throws MalformedURLException {
            FileUrlResource resource = new FileUrlResource(env.getProperty("applicant.batch.archive.resource"));
            return resource;
        }

        @Bean
        WritableResource sourceResource(Environment env) throws MalformedURLException {
            FileUrlResource resource = new FileUrlResource(env.getProperty("applicant.batch.source.resource"));
            return resource;
        }
    }
}
