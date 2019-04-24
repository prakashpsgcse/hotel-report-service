package com.target.report.generator.rest;

import java.util.stream.Collectors;

import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.mongodb.MongoException;
import com.target.report.generator.domain.ApplicationCustomErrorResponse;
import com.target.report.generator.utils.Constants;

/**
 * Exception handler for application
 * 
 * @author pprakash
 *
 */
@ControllerAdvice
public class ReportControllerAdvice extends ResponseEntityExceptionHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(ReportControllerAdvice.class);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        LOGGER.error("Exception encountered", ex.getMessage());
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("\n"));
        return (ResponseEntity) new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ KafkaException.class })
    public ResponseEntity<ApplicationCustomErrorResponse> handleKafkaException(KafkaException ex) {
        LOGGER.error("Exception encountered", ex.getMessage());
        ApplicationCustomErrorResponse response = new ApplicationCustomErrorResponse();
        response.setErrorCode(Constants.KAFKA_ERROR_CODE);
        response.setErrorDetails(Constants.ERROR_MSG);

        return new ResponseEntity<ApplicationCustomErrorResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MongoException.class)
    public ResponseEntity<ApplicationCustomErrorResponse> handleMongoException(Exception ex) {
        LOGGER.error("Exception encountered {} ", ex.getMessage());
        ApplicationCustomErrorResponse response = new ApplicationCustomErrorResponse();
        response.setErrorCode(Constants.MONGO_ERROR_CODE);
        response.setErrorDetails(Constants.ERROR_MSG);

        return new ResponseEntity<ApplicationCustomErrorResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleUnauthorizedException(Exception ex) {
        LOGGER.error("Exception encountered", ex.getMessage());
        return new ResponseEntity<String>(Constants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApplicationCustomErrorResponse> handleGenericException(Exception ex) {
        LOGGER.error("Exception encountered", ex.getMessage());
        ApplicationCustomErrorResponse response = new ApplicationCustomErrorResponse();
        response.setErrorCode(Constants.GENERIC_ERROR_CODE);
        response.setErrorDetails(Constants.ERROR_MSG);

        return new ResponseEntity<ApplicationCustomErrorResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
