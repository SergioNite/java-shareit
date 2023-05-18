package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.DublicateEmailErrorException;
import ru.practicum.shareit.user.exceptions.EmailErrorException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUserModel(userDto, null);
        try {
            log.debug("Добавление юзера: {}", user);
            return userMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new DublicateEmailErrorException("createUser: duplicate email");
        } catch (ConstraintViolationException e){
            throw new EmailErrorException("createUser: email error");
        }
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = userMapper.toUserModel(userDto, userId);
        User updatedUser = setUpdatedUserFields(userId, user);
        try {
            user = userRepository.save(updatedUser);
            return userMapper.toUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new DublicateEmailErrorException("updateUser: can not save empty user info by id " + userId);
        }
    }

    private User setUpdatedUserFields(long userId, User user) {
        User updatedUser = getUserById(userId);

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }

        String newEmail = user.getEmail();

        if (newEmail != null) {
            if (!newEmail.isBlank()) {
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
