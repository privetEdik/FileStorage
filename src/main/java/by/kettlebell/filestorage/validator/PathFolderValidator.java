package by.kettlebell.filestorage.validator;

import by.kettlebell.filestorage.exception.Error;
import by.kettlebell.filestorage.exception.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class PathFolderValidator implements Validator {
    private static final String regex = "[\\w]";

    @Override
    public void isValid(String path) throws ValidationException {

        if (path == null || path.isBlank()) {
            throw new ValidationException(Error.of("404", "The request path must not empty"));
        }

        boolean firstCharMustBeSlash = path.startsWith("/");
        boolean lastCharMustBeNotSlash = !path.endsWith("/");
        boolean withoutBackspace = !path.contains(" ");
        boolean NotFoldersWithEmptyName = !path.contains("//");

        boolean onlyLettersNumbersDashAndSlash = path
                .replaceAll("/", "")
                .replaceAll("-", "")
                .replaceAll(regex, "").isEmpty();


        if (!firstCharMustBeSlash) {
            throw new ValidationException(Error.of("404", "The first slash  is missing in the request path"));
        } else if (!lastCharMustBeNotSlash) {
            throw new ValidationException(Error.of("404", "The request path must not end with a slash"));
        } else if (!withoutBackspace) {
            throw new ValidationException(Error.of("404", "The request path must not have spaces"));
        } else if (!NotFoldersWithEmptyName) {
            throw new ValidationException(Error.of("404", "The request path should not have unnamed folders"));
        } else if (!onlyLettersNumbersDashAndSlash) {
            throw new ValidationException(Error.of("404", "The request path must contain only letters, numbers and dashes in folder names"));
        }

    }


}
