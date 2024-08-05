package by.kettlebell.filestorage.exception.user;

import by.kettlebell.filestorage.exception.ApplicationException;
import by.kettlebell.filestorage.exception.Error;

public class UserAlreadyExistsException extends ApplicationException {
    public UserAlreadyExistsException(Error error) {
        super(error);
    }

}
