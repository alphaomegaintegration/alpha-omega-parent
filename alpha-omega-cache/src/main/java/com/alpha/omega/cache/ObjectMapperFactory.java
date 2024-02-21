package com.alpha.omega.cache;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperFactory {

    ObjectMapper createObjectMapper(Scope scope);

    public enum Scope{
        SINGLETON, PROTOTYPE;
    }

}
