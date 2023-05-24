package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    JacksonTester<BookingDtoRequest> json;

    @SneakyThrows
    @Test
    void bookingDtoTest() {
        BookingDtoRequest bookingDtoShort = BookingDtoRequest.builder()
                .id(1L)
                .itemId(1L)
                .build();
        JsonContent<BookingDtoRequest> jsonTest = json.write(bookingDtoShort);

        assertThat(jsonTest).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingDtoShort.getId().intValue());
    }
}