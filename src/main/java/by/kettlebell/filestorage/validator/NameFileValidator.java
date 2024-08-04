package by.kettlebell.filestorage.validator;

import by.kettlebell.filestorage.exception.ApplicationException;
import by.kettlebell.filestorage.exception.Error;
import by.kettlebell.filestorage.exception.validation.NameFileValidationException;
import by.kettlebell.filestorage.exception.validation.NameFolderValidationException;

public class NameFileValidator implements Validator {
    @Override
    public void isValid(String nameFile) throws ApplicationException {

        String[] arr = nameFile.split("\\.");
        if (arr.length != 2 ||
                arr[0].isBlank() ||
                arr[1].isBlank() ||
                !arr[0].replaceAll("[\\w]","").replaceAll("-","").isEmpty() ||
                !arr[1].replaceAll("[\\w]","").replaceAll("-","").isEmpty() ||
                !(arr[0].length()>=1 & arr[0].length()<=50) ||
                !(arr[1].length()>=3 & arr[1].length()<=10)
        ) {
            throw new NameFileValidationException(Error.of("404", "the full file name must consist of the name (from 1 to 50 characters), '.' and extension (from 3 to 10 characters)"));
        }
    }
}
