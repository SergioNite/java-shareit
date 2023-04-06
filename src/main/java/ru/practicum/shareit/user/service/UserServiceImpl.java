package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserDao;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{
    private UserDao userDao;

    @Override
    public User createUser(User user) {
        return userDao.createUser(user);
    }

    @Override
    public User updateUser(long userId, User user) {
        return userDao.updateUser(userId, user);
    }

    @Override
    public User findUserById(long userId) {
        return userDao.findUserById(userId);
    }

    @Override
    public void deleteUserById(long userId) {
        userDao.deleteUserById(userId);
    }

    @Override
    public List<User> findAllUsers() {
        return userDao.findAllUsers();
    }
}
