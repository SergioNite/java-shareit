package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.DublicateEmailErrorException;
import ru.practicum.shareit.user.exceptions.EmailErrorException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("testName")
            .email("testEmail@gmail.com")
            .build();

    @SneakyThrows
    @Test
    void createUser_whenValid_thenSaveAndReturnUser() {
        Mockito.when(userService.createUser(Mockito.any())).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));
        verify(userService, Mockito.times(1)).createUser(Mockito.any());
    }

    @SneakyThrows
    @Test
    void createUser_whenUsernameInvalid_thenReturnBadRequest() {
        final UserDto userTestDto = UserDto.builder()
                .id(1L)
                .name(null)
                .email("testEmail@gmail.com")
                .build();
        Mockito.when(userRepository.save(any())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userTestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, Mockito.never()).createUser(Mockito.any());

    }

    @SneakyThrows
    @Test
    void createUser_whenEmailInvalid_thenReturnBadRequest() {
        final UserDto userTestDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email(null)
                .build();
        Mockito.when(userRepository.save(any())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userTestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, Mockito.never()).createUser(Mockito.any());

    }

    @SneakyThrows
    @Test
    void createUser_whenDublicateEmail_thenReturnBadRequest() {
        Mockito.when(userService.createUser(any())).thenThrow(DublicateEmailErrorException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(userRepository, never()).save(any());
    }

    @SneakyThrows
    @Test
    void createUser_whenUserAlreadyExists_thenReturnBadRequest() {
        Mockito.when(userService.createUser(any())).thenThrow(EmailErrorException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userRepository, never()).save(any());
    }


    @SneakyThrows
    @Test
    void updateUser_whenInputValid_thenSaveAndReturnUser() {
        UserDto userDtoUpd = UserDto.builder()
                .id(1L)
                .name("newTestName")
                .email("newTestEmail@gmail.com")
                .build();
        Mockito.when(userService.updateUser(Mockito.anyLong(), Mockito.any())).thenReturn(userDtoUpd);
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoUpd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDtoUpd.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDtoUpd.getEmail()), String.class))
                .andExpect(jsonPath("$.id", is(userDtoUpd.getId()), Long.class));
        verify(userService, times(1)).updateUser(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserIdInvalid_thenReturnNotFound() {
        UserDto userDtoUpd = UserDto.builder()
                .id(1L)
                .name("newTestName")
                .email("newTestEmail@gmail.com")
                .build();
        Mockito.when(userService.updateUser(Mockito.anyLong(), Mockito.any())).thenThrow(UserNotFoundException.class);
        Mockito.when(userService.setUpdatedUserFields(anyLong(), any())).thenThrow(EmailErrorException.class);
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/555")
                        .content(mapper.writeValueAsString(userDtoUpd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void deleteUserById_whenValidInput_thenDelete() {
        Mockito.doNothing().when(userRepository).deleteById(anyLong());
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void deleteUserById_whenUserDoesNotExists_thenBadRequest() {
        Mockito.when(userRepository.existsById(1000L)).thenReturn(false);
        Mockito.doThrow(new UserNotFoundException("err")).when(userService).deleteUserById(anyLong());
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1000")
                        .header("X-Sharer-User-Id", 1000L))
                .andExpect(status().isNotFound());
        verify(userService, times(1)).deleteUserById(anyLong());
    }


    @SneakyThrows
    @Test
    void findUserById_whenValidInput_thenReturnUser() {
        Mockito.when(userService.findUserById(Mockito.anyLong())).thenReturn(userDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void findUserById_whenUserDoesNotExist_thenReturnNotFound() {
        Mockito.doThrow(new UserNotFoundException("err")).when(userService).findUserById(anyLong());
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void findAllUsers_thenReturnUserList() {
        UserDto userDtoTwo = UserDto.builder()
                .id(2L)
                .name("testNameTwo")
                .email("testEmailTwo@gmail.com")
                .build();
        UserDto userDtoThree = UserDto.builder()
                .id(3L)
                .name("testNameThree")
                .email("testEmailThree@gmail.com")
                .build();
        List<UserDto> userDtoList = List.of(userDto, userDtoTwo, userDtoThree);
        Mockito.when(userService.findAllUsers()).thenReturn(userDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }
}