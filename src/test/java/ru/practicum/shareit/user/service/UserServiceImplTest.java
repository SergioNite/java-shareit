package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.DublicateEmailErrorException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserService userService;

    @BeforeEach
    void beforeEach() {
        userRepository = Mockito.mock(UserRepository.class);
        userMapper = Mappers.getMapper(UserMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    void createUser_whenValid_thenSavedUser() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(userMapper.toUserModel(userDto, 1L));

        UserDto userDtoTest = userService.createUser(userDto);

        Mockito.verify(userRepository).save(Mockito.any());
        assertEquals(userDto.getId(), userDtoTest.getId());
        assertEquals(userDto.getName(), userDtoTest.getName());
        assertEquals(userDto.getEmail(), userDtoTest.getEmail());
    }

    @Test
    void createUser_whenDublicateEmail_thenDublicateEmailErrorException() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();

        Mockito.when(userRepository.save(Mockito.any())).thenThrow(DublicateEmailErrorException.class);

        assertThrows(DublicateEmailErrorException.class,
                () -> userService.createUser(userDto));
    }

    @Test
    void updateUser_whenValid_thenSavedUser() {
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
    void updateUser_whenNameChange_thenReturnedWithNewNameUser() {
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
    void updateUser_whenWrongUserId_thenUserNotFoundException() {
        long wrongUserId = -1L;
        User user = User.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();
        UserDto userDtoExpected = UserDto.builder()
                .id(1L)
                .name("newTestName")
                .email("newTestEmail@gmail.com")
                .build();

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        UserDto actualUser = userService.updateUser(user.getId(), userDtoExpected);
        assertEquals(userDtoExpected, actualUser);
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(wrongUserId, userDtoExpected));

    }


    @Test
    void deleteUserById_whenUserValid_thenDelete() {
        User user = User.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        userService.deleteUserById(user.getId());
        Mockito.verify(userRepository).deleteById(Mockito.anyLong());
    }

    @Test
    void deleteUserById_whenUserNotFound_thenUserNotFoundException() {
        long wrongUserId = -1L;
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(wrongUserId));
    }

    @Test
    void findUserById_whenUserFound_thenReturnedUser() {
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
    void findUserById_whenUserNotFound_thenUserNotFoundException() {
        long userId = 100L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(userId));
    }

    @Test
    void findAll_thenReturnValid() {
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

    @Test
    void getUserById_whenExists_thenReturnUser() {
        User expectedUser = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expectedUser));
        User resultUser = userService.getUserById(1L);
        assertEquals(expectedUser, resultUser);
    }

    @Test
    void getUserById_whenDoesNotExists_thenThrowException() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(0L));
    }


}