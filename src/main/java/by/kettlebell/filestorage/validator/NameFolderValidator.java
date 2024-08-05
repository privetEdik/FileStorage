package by.kettlebell.filestorage.validator;

import by.kettlebell.filestorage.exception.Error;
import by.kettlebell.filestorage.exception.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class NameFolderValidator implements Validator {
    @Override
    public void isValid(String nameFolder) throws ValidationException {

        //only letters numbers dash underscore
        if (nameFolder == null ||
                nameFolder.isBlank() ||
                !nameFolder.replaceAll("-", "")
                        .replaceAll("[\\w]", "").equals("")) {
            throw new ValidationException(Error.of("404", "The name folder must contain only letters, numbers, dashes and underscore"));
        }
    }
}
