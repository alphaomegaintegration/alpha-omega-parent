package com.globalpayments.security.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LobConverter implements AttributeConverter<String, Object> {

    private static Logger logger = LoggerFactory.getLogger(LobConverter.class);

    /*
    The conversion from binary to BIT is unsupported.
     */

    @Override
    public Object convertToDatabaseColumn(String str) {
        return null;
    }

    @Override
    public String convertToEntityAttribute(Object object) {
        logger.info("We have class of {}",object.getClass().getName());
        return "";
    }
}
