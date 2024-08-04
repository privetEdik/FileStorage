package by.kettlebell.filestorage.exception.validation;

import by.kettlebell.filestorage.exception.Error;

public class NameFileValidationException extends ValidationException{
    public NameFileValidationException(Error error) {
        super(error);
    }
}
