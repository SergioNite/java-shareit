package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStatus;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperShortTest {

    @Test
    void testBookingToBookingDtoShort() {
        User booker = User.builder().id(10L).name("test").email("test@google.com").build();
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build();
        BookingDtoItem bookingDtoItem = BookingMapperShort.bookingToBookingDtoShort(booking);
        assertEquals(booker.getId(),bookingDtoItem.getBookerId());
        assertEquals(booking.getId(),bookingDtoItem.getId());
    }
}