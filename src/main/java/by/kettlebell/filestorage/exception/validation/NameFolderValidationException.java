package by.kettlebell.filestorage.exception.validation;

import by.kettlebell.filestorage.exception.Error;

public class NameFolderValidationException extends ValidationException{
    public NameFolderValidationException(Error error) {
        super(error);
    }
}
