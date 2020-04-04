package com.swp493.ivb.config;

import java.util.NoSuchElementException;

import com.swp493.ivb.common.view.Payload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler({ NoSuchElementException.class })
    public ResponseEntity<?> handleNoSuchElementException(Exception ex, WebRequest request) {
        log.info(ex.getMessage(),ex);
        return Payload.failureResponse("Invalid id");
    }

    @ExceptionHandler({ ResponseStatusException.class})
    public ResponseEntity<?> handleResponseStatusEx(ResponseStatusException ex, WebRequest request) throws Exception{   
        switch (ex.getStatus()) {
            case BAD_REQUEST:
                return Payload.failureResponse(ex.getMessage());
            case FORBIDDEN:
                return ResponseEntity.status(ex.getStatus()).body(new Payload<>().fail("Resource access not allowed"));
            default:
                break;
        }
        return ResponseEntity.status(ex.getStatus()).build();
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<?> handleGeneralException(Exception ex, WebRequest request) {
        log.error("Internal error", ex);
        return Payload.internalError();
    }

}