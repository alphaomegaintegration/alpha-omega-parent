package com.alpha.omega.core.utils;

import org.springframework.data.domain.PageRequest;

import java.util.function.Function;

public class CoreUtils {


    public static Function<PageRequest, Integer> calculateSkip = pageRequest -> {
        return (pageRequest.getPageNumber() - 1) * pageRequest.getPageSize();
    };
}
