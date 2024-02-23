package com.alpha.omega.cache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FalseNoOpFuture implements Future<Boolean> {
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Boolean get() throws InterruptedException, ExecutionException {
        return Boolean.FALSE;
    }

    @Override
    public Boolean get(long timeout,  TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return Boolean.FALSE;
    }
}
