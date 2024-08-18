package by.kettlebell.filestorage.validator;

import by.kettlebell.filestorage.exception.Error;
import by.kettlebell.filestorage.exception.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PathFileValidator implements Validator {

    private final PathFolderValidator validator;

    @Override
    public void isValid(String path) throws ValidationException {

        if (path == null ||
                path.isBlank() ||
                !path.contains(".") || //if there is no point - error
                path.replaceFirst("\\.", "").contains(".") || // if more than one point
                path.contains("/.") || // if name file empty
                path.endsWith(".")) { // if extension empty
            throw new ValidationException(Error.of("404", "Invalid name file, pattern:  ' from 1 to 10 characters . 3 characters file extension' a dash is allowed"));
        }

        validator.isValid(path.replaceFirst("\\.", "-"));

    }
}
