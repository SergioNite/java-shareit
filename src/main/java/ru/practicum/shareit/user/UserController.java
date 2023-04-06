package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserMapper mapper;
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Validated @RequestBody UserDto userDto) {
        User user = mapper.toUserModel(userDto, null);
        return mapper.toUserDto(userService.createUser(user));
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return mapper.mapUsersToDtoList(userService.findAllUsers());
    }
    @GetMapping("/{userId}")
    public UserDto findUserById(@NotNull
                                @Min(1)
                                @PathVariable Long userId) {
        return mapper.toUserDto(userService.findUserById(userId));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@NotNull
                              @Min(1)
                              @PathVariable Long userId,
                              @Validated @RequestBody UserDto userDto) {
        User user = mapper.toUserModel(userDto, userId);
        return mapper.toUserDto(userService.updateUser(userId, user));
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@NotNull
                               @Min(1)
                               @PathVariable Long userId) {
        userService.deleteUserById(userId);
    }

}
