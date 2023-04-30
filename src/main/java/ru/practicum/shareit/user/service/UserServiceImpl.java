package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.DublicateEmailErrorException;
import ru.practicum.shareit.user.exceptions.EmailErrorException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUserModel(userDto, null);
        if (user.getEmail() == null || user.getName() == null) {
            throw new EmailErrorException("Empty user values");
        }
        try {
            user = userRepository.save(user);
            log.debug("Добавлен юзер: {}", user);
            return userMapper.toUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new DublicateEmailErrorException("createUser: duplicate email");
        }
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = userMapper.toUserModel(userDto, userId);
        User updatedUser = setUpdatedUserFields(userId, user);
        try {
            user = userRepository.save(updatedUser);
            return userMapper.toUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new DublicateEmailErrorException("updateUser: cannt save empty user info by id " + userId);
        }
    }

    private User setUpdatedUserFields(long userId, User user) {
        User updatedUser = getUserById(userId);

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }

        String newEmail = user.getEmail();

        if (newEmail != null) {
            if (!newEmail.isBlank()){
                updatedUser.setEmail(newEmail);
            } else {
                throw new EmailErrorException("Указанный E-mail (" + newEmail + ") используется другим юзером");
            }
        }
        return updatedUser;
    }

    @Override
    public UserDto findUserById(long userId) {
        return userMapper.toUserDto(getUserById(userId));
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("getUserById: user not found id=%d !", userId))
        );
    }

    @Override
    public void deleteUserById(long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException("deleteUserById: Не найден юзер по id");
        }
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
