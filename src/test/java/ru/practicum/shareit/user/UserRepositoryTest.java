package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void BeforeEach(){
        userRepository.save(User.builder()
                .name("userName")
                .email("userEmail@google.com")
                .build());
    }

    @AfterEach
    public void deleteUsers(){
        userRepository.deleteAll();
    }

    @Test
    void findByEmail() {
        Optional<User> actualUser = userRepository.findByEmail("userEmail@google.com");
        assertTrue(actualUser.isPresent());
    }
}