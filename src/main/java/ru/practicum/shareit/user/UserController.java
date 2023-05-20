package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.EmailErrorException;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody @Validated(Create.class) UserDto userDto) throws EmailErrorException {
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@NotNull
                                @Min(1)
                                @PathVariable Long userId) {
        return userService.findUserById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@NotNull
                              @Min(1)
                              @PathVariable Long userId,
                              @RequestBody @Validated(Update.class) UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@NotNull
                               @Min(1)
                               @PathVariable Long userId) {
        userService.deleteUserById(userId);
    }

}
