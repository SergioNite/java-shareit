package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingStateTest {

    @Test
    void values() {
        assertEquals(BookingState.ALL,BookingState.ALL);
    }

    @Test
    void valueOf() {
        assertEquals(BookingState.valueOf("ALL"), BookingState.ALL);
    }
}