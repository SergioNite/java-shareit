package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;
    UserService userService;

    @BeforeEach
    void beforeEach() {
        userRepository = Mockito.mock(UserRepository.class);
        userMapper = Mappers.getMapper(UserMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    void save() {
        User user = User.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        UserDto userDtoTest = userService.createUser(userDto);

        assertEquals(userDto.getId(), userDtoTest.getId());
        assertEquals(userDto.getName(), userDtoTest.getName());
        assertEquals(userDto.getEmail(), userDtoTest.getEmail());
    }

    @Test
    void update() {
        User user = User.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();
        UserDto userDtoForUpdate = UserDto.builder()
                .name("newTestName")
                .email("newTestEmail@gmail.com")
                .build();

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        UserDto updatedUserDtoTest = userService.updateUser(user.getId(), userDtoForUpdate);
        assertEquals(user.getId(), updatedUserDtoTest.getId());
        assertEquals(user.getName(), updatedUserDtoTest.getName());
        assertEquals(user.getEmail(), updatedUserDtoTest.getEmail());
    }

    @Test
    void updateWithOnlyNameChange() {
        User user = User.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();
        UserDto userDtoForUpdate = UserDto.builder()
                .name("newTestName")
                .build();

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        UserDto updatedUserDtoTest = userService.updateUser(user.getId(), userDtoForUpdate);
        assertEquals(user.getId(), updatedUserDtoTest.getId());
        assertEquals(user.getName(), updatedUserDtoTest.getName());
        assertEquals(user.getEmail(), updatedUserDtoTest.getEmail());
    }

    @Test
    void updateWithWrongUserId() {
        User user = User.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();
        UserDto userDtoForUpdate = UserDto.builder()
                .name("newTestName")
                .email("newTestEmail@gmail.com")
                .build();

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        Mockito.when(userRepository.findById(-1L)).thenReturn(Optional.of(user));

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(user.getId(), userDtoForUpdate));
    }

    @Test
    void delete() {
        User user = User.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(1L));
    }

    @Test
    void findById() {
        User user = User.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        UserDto userDtoTest = userService.findUserById(user.getId());
        assertEquals(user.getId(), userDtoTest.getId());
        assertEquals(user.getName(), userDtoTest.getName());
        assertEquals(user.getEmail(), userDtoTest.getEmail());
    }

    @Test
    void findAll() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        List<User> userList = List.of(userOne, userTwo);

        Mockito.when(userRepository.findAll()).thenReturn(userList);

        List<UserDto> userDtoList = userService.findAllUsers();
        assertEquals(userList.size(), userDtoList.size());
        assertEquals(userList.get(0).getId(), userDtoList.get(0).getId());
        assertEquals(userList.get(0).getName(), userDtoList.get(0).getName());
        assertEquals(userList.get(0).getEmail(), userDtoList.get(0).getEmail());
    }
}