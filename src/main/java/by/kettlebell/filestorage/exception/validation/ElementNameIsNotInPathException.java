package by.kettlebell.filestorage.exception.validation;

import by.kettlebell.filestorage.exception.Error;

public class ElementNameIsNotInPathException extends ValidationException{
    public ElementNameIsNotInPathException(Error error) {
        super(error);
    }
}
