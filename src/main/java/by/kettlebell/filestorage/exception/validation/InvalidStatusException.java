package by.kettlebell.filestorage.exception.validation;

import by.kettlebell.filestorage.exception.Error;

public class InvalidStatusException extends ValidationException{
    public InvalidStatusException(Error error) {
        super(error);
    }
}
