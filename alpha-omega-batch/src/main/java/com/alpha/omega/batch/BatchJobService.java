package com.alpha.omega.batch;

import com.unhrc.bims.Constants;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

public class BatchJobService<T> {

    private static final Logger logger = LoggerFactory.getLogger(BatchJobService.class);

    protected JobRepository jobRepository;
    protected JobLauncher jobLauncher;
    protected BatchJobFactory<T> batchJobFactory;

    protected Function<JobExecution, BatchResponse> convertToBatchResponse(){
        return jobExecution -> {
            return BatchResponse.builder()
                    //.jobExecution(jobExecution)
                    .jobInstance(jobExecution.getJobInstance())
                    .createTime(jobExecution.getCreateTime())
                    .executionContext(jobExecution.getExecutionContext())
                    .exitStatus(jobExecution.getExitStatus())
                    .jobName(jobExecution.getJobInstance().getJobName())
                    .build();
        };
    }

    public BatchResponse startJob(BatchRequest<T> batchRequest){
        Map<String, JobParameter<?>> jobsMap = batchRequest.getJobParameters().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> new JobParameter<>(entry.getValue(), String.class)));
;        JobExecution jobExecution = null;
        try {
            logger.info("Got batchApplicantRequest => {}");
            Job job = batchJobFactory.createJobFromRequest(batchRequest);
            jobsMap.put(Constants.CORRELATION_ID, new JobParameter<>(batchRequest.getCorrelationId(), String.class));
            jobExecution = jobLauncher.run(job, new JobParameters(jobsMap));
        } catch (JobExecutionAlreadyRunningException e) {
            throw new BatchException(e);
        } catch (JobRestartException e) {
            throw new BatchException(e);
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new BatchException(e);
        } catch (JobParametersInvalidException e) {
            throw new BatchException(e);
        }
        logger.debug("JobExecutionId => {}",jobExecution.getJobId());

        return convertToBatchResponse().apply(jobExecution);
    }

    /*
    private Job extractJobFromRequest(BatchApplicantRequest batchApplicantRequest) {
        Job job = null;
        if (batchApplicantRequest.getApplicants() == null || batchApplicantRequest.getApplicants().isEmpty()){
            job = csvJob;
            validateCsvJobRequest(batchApplicantRequest);
        } else {
            job = applicantLoadBatchJobFactory.createJobFromRequest(batchApplicantRequest);
        }
        return job;
    }

     */


    public BatchResponse getJobStatus(BatchRequest<T> batchApplicantRequest){
        BatchResponse batchApplicantResponse = this.getJobExecution(batchApplicantRequest);
        JobExecution jobExecution = batchApplicantResponse.getJobExecution();
        return convertToBatchResponse().apply(jobExecution);
    }

    public BatchResponse getJobExecution(BatchRequest<T> batchRequest){
        Map<String, JobParameter<?>> jobsMap = batchRequest.getJobParameters().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> new JobParameter<>(entry.getValue(), String.class)));
        Job job = batchJobFactory.createJobFromRequest(batchRequest);
        jobsMap.put(Constants.CORRELATION_ID, new JobParameter<>(batchRequest.getCorrelationId(), String.class));
        JobExecution jobExecution = jobRepository.getLastJobExecution(job.getName(), new JobParameters(jobsMap));
        return convertToBatchResponse().apply(jobExecution);
    }

}
