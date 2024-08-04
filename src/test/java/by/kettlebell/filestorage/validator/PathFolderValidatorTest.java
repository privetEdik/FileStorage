package by.kettlebell.filestorage.validator;

import by.kettlebell.filestorage.exception.validation.ValidationException;
import org.junit.jupiter.api.Test;

class PathFolderValidatorTest {

    @Test
    void isValid() {
        PathFolderValidator path = new PathFolderValidator();
       try {
           path.isValid("//rr-r");
       }catch (ValidationException e) {
           System.out.println(e.getError().getMessage());
       }


    }
}