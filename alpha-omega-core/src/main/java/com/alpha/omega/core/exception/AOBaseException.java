package com.pwc.base.exceptions;

import com.pwc.base.utils.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.ws.rs.core.Response;
import java.util.Objects;

import static com.pwc.base.utils.BaseConstants.ALERT;
import static com.pwc.base.utils.BaseUtil.convertObjectToJsonString;

@Slf4j
public class PwcBaseException extends RuntimeException{

    private static final long serialVersionUID = -4024139128663973568L;

    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public PwcBaseException() {
        super();
    }

    public PwcBaseException(String message) {
        super(message);
    }

    public PwcBaseException(Throwable throwable) {
        super(throwable);
    }

    public PwcBaseException(String message, Throwable throwable) {
        super(message + throwable.getMessage(), throwable);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public PwcBaseException(HttpStatus status, String message) {
        super(message);
        setStatus(status);
    }

    public PwcBaseException(Response response) {
        super(response.readEntity(String.class));
        setStatus(HttpStatus.valueOf(response.getStatus()));
    }

    public PwcBaseException(ResponseEntity<Object> responseEntity) {
        super(Objects.nonNull(responseEntity.getBody()) ? convertObjectToJsonString(responseEntity.getBody()) : "message not available");
        setStatus(HttpStatus.valueOf(responseEntity.getStatusCode().value()));
    }

    public PwcBaseException(Response response, String message) {
        super((Objects.nonNull(message) ? message + " " : "" ) + response.readEntity(String.class));
        int status = response.getStatus();
        setStatus(HttpStatus.valueOf(status));
        if (status >= 500) {
            log.error(ALERT + Encode.forJava(message));
        }
    }

    public PwcBaseException(ResponseEntity<Object> response, String message) {
        super((Objects.nonNull(message) ? message + " " : "" ) + BaseUtil.convertObjectToJsonString(response.getBody()));
        int status = response.getStatusCode().value();
        setStatus(HttpStatus.valueOf(response.getStatusCode().value()));
        if (status >= 500) {
            log.error(ALERT + Encode.forJava(message));
        }
    }

    public PwcBaseException(int status, String responseString, String message) {
        super((Objects.nonNull(message) ? message + " " : "" ) + responseString);
        setStatus(HttpStatus.valueOf(status));
    }

    public PwcBaseException(HttpStatus status, String message,Throwable t){
        this(message,t);
        setStatus(status);
    }
}
