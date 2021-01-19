package au.com.eventsecretary.client;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorException extends RuntimeException {
    private final List<ValidationError> validationErrorList;

    public ValidationErrorException(String field, String code, String message) {
        validationErrorList = new ArrayList<>();
        ValidationError validationError = new ValidationError();
        ValidationError.ErrorSource errorSource = new ValidationError.ErrorSource();
        errorSource.setPointer(field);
        validationError.setSource(errorSource);
        validationError.setCode(code);
        validationError.setTitle(message);
        validationErrorList.add(validationError);
    }

    public ValidationErrorException(List<ValidationError> validationErrorList) {
        this.validationErrorList = validationErrorList;
    }


    public List<ValidationError> getValidationErrors() {
        return validationErrorList;
    }
}
