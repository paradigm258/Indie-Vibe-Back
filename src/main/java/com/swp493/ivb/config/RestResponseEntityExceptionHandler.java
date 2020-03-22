package com.swp493.ivb.config;

import java.util.NoSuchElementException;

import com.swp493.ivb.common.view.Payload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<?> handleAccessDeniedException(Exception ex, WebRequest request) {
        return Payload.failureResponse(ex.getMessage());
    }

    @ExceptionHandler({ NoSuchElementException.class })
    public ResponseEntity<?> handleNoSuchElementException(Exception ex, WebRequest request) {
        return Payload.failureResponse("Invalid id");
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<?> handleGeneralException(Exception ex, WebRequest request) {
        log.error("Internal", ex);
        return Payload.internalError();
    }

}