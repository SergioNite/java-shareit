package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User createUser(User user);

    User updateUser(long userId, User user);

    User findUserById(long userId);

    void deleteUserById(long userId);

    List<User> findAllUsers();
}
