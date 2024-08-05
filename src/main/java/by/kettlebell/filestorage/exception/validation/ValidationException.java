package by.kettlebell.filestorage.exception.validation;

import by.kettlebell.filestorage.exception.ApplicationException;
import by.kettlebell.filestorage.exception.Error;

public class ValidationException extends ApplicationException {

    public ValidationException(Error error) {
        super(error);
    }
}
