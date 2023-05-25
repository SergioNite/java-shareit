package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {
    UserMapper userMapper = new UserMapper();

    @Test
    void mapUsersToDtoList() {
        User user = User.builder().id(10L).name("test").email("test@google.com").build();
        List<User> userList = List.of(user);
        List<UserDto> userDtoList = userMapper.mapUsersToDtoList(userList);
        assertEquals(userList.size(),userDtoList.size());
        assertEquals(userList.get(0).getId(),userDtoList.get(0).getId());

    }
}