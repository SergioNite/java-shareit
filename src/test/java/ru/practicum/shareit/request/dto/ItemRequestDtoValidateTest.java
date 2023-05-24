package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;


class ItemRequestDtoValidateTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @CsvSource({",'   '", "''"})
    void testWrongDescription(String description) {
        final ItemRequestDto itemDto = ItemRequestDto.builder()
                .description(description)
                .build();

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemDto);
        Assertions.assertFalse(violations.isEmpty());
    }

}