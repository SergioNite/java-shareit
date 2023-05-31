package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    User setUpdatedUserFields(long userId, User user);

    UserDto findUserById(long userId);

    User getUserById(long userId);

    void deleteUserById(long userId);

    List<UserDto> findAllUsers();
}
