package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@Validated(Create.class) @RequestBody UserDto user) {
        log.info("User {} was added", user);
        return userClient.post(user);
    }

    @PatchMapping(path = "/{userId}")
    //@Cacheable("updateUser")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @Validated(Update.class) @RequestBody UserDto patchUser) {
        log.info("User {} was changed", userId);
        return userClient.updateUser(userId, patchUser);
    }

    @GetMapping("/{userId}")
    //@Cacheable("getUser")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.info("Get user {}", userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    //@Cacheable("deleteUser")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("User was deleted {}", userId);
        return userClient.deleteUser(userId);
    }

    @GetMapping
    //@Cacheable("getOneRequest")
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }
}
