package by.kettlebell.filestorage.validator.annotation.user;

import by.kettlebell.filestorage.dto.entity.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserInfoValidator implements ConstraintValidator<UserInfo, User> {
    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {

        return user.getUsername().replaceAll("[\\w]", "").replaceAll("-", "").isEmpty();

    }
}
