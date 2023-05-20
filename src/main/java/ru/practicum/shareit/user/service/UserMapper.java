package ru.practicum.shareit.user.service;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toUserModel(UserDto userDto, Long userId) {
        return new User(userId, userDto.getName(), userDto.getEmail());
    }

    public List<UserDto> mapUsersToDtoList(List<User> users) {
        List<UserDto> result = new ArrayList<>();
        for (User user : users) {
            result.add(toUserDto(user));
        }
        return result;
    }
}
