package by.kettlebell.filestorage.validator;

import by.kettlebell.filestorage.exception.ApplicationException;

public interface Validator {
    void isValid(String object) throws ApplicationException;
}
