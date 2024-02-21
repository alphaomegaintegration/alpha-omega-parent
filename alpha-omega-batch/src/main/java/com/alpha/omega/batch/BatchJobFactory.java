package com.alpha.omega.batch;

import org.springframework.batch.core.Job;

@FunctionalInterface
public interface BatchJobFactory<T> {
    public Job createJobFromRequest(BatchRequest<T> batchRequest);


}
