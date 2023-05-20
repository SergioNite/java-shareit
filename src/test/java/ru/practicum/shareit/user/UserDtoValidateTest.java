package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;


public class UserDtoValidateTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void tesNameIsEmptyOrNull() {
        final UserDto userDto = UserDto.builder()
                .name(null)
                .email("email@mail.com")
                .build();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        Assertions.assertFalse(!violations.isEmpty());

    }

    @Test
    void testEmailIsEmptyOrNull() {
        final UserDto userDto = UserDto.builder()
                .name("name")
                .email(null)
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        Assertions.assertFalse(!violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "email @google.com", "google.com"})
    void testNotValidEmail(String email) {
        final UserDto userDto = UserDto.builder()
                .name("name")
                .email(email)
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        Assertions.assertFalse(violations.isEmpty());
    }
}
