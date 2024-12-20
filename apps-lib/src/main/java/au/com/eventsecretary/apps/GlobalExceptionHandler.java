package au.com.eventsecretary.apps;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnacceptableException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.ValidationException;
import au.com.eventsecretary.client.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception handler that overrides some of the logic of exceptions handling in DefaultExceptionHandler.
 * Enriching with more information on certain exceptions.
 */
@ControllerAdvice
@AutoConfiguration
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static class ApiError {
        String message;
        String code;
        public String getMessage() {
            return message;
        }
        public String getCode() { return code; }
        public String getSupportEmail() { return  "support@eventsecretary.com.au"; }
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalArgumentException.class)
    public void handleException(IllegalArgumentException e) {
        LOG.error("handleException", e);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity handleNotFoundException(ResourceNotFoundException e) {
        ApiError apiError = new ApiError();
        apiError.message = e.getMessage();
        return new ResponseEntity(apiError, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity handleExistsException(ResourceExistsException e) {
        ApiError apiError = new ApiError();
        apiError.message = e.getMessage();
        return new ResponseEntity(apiError, HttpStatus.PRECONDITION_FAILED);
    }

    @ResponseBody
    @ExceptionHandler(UnacceptableException.class)
    public ResponseEntity handleUnacceptableException(UnacceptableException e) {
        ApiError apiError = new ApiError();
        apiError.message = e.getMessage();
        return new ResponseEntity(apiError, HttpStatus.NOT_ACCEPTABLE);
    }

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity handleExistsException(ValidationException e) {
        ApiError apiError = new ApiError();
        apiError.message = e.getMessage();
        apiError.code = e.getCode();
        return new ResponseEntity(apiError, HttpStatus.PRECONDITION_FAILED);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public void handleUnauthorizedException(UnauthorizedException e) {
        try {
            if (!e.noCredentitals()) {
                Thread.sleep(3000);
            }
        } catch (InterruptedException ex) {
        }
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(UnexpectedSystemException.class)
    public void handleUnexpectedException(UnexpectedSystemException e) {
        LOG.error("handleUnexpectedException", e);
    }

}
