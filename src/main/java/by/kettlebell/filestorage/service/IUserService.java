package by.kettlebell.filestorage.service;

import by.kettlebell.filestorage.dto.UserDto;
import by.kettlebell.filestorage.dto.entity.User;

public interface IUserService {
    User registerNewUserAccount(UserDto userDto);
}
