package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    void testMapToModel() {
        LocalDateTime now = LocalDateTime.now();
        BookingDtoRequest bookingDtoShort = BookingDtoRequest.builder()
                .id(1L)
                .itemId(1L)
                .start(now)
                .end(now)
                .build();
        User user = User.builder().id(10L).name("test").email("test@google.com").build();
        Item item = Item.builder().id(1L).name("testName").available(false).build();
        Booking expectedBooking = BookingMapper.mapToModel(bookingDtoShort,item,user);
        assertEquals(item.getId(),expectedBooking.getItem().getId());
        assertEquals(user.getId(),expectedBooking.getBooker().getId());
        assertEquals(now,expectedBooking.getEnd());
    }

    @Test
    void testMapToDto() {
        User user = User.builder().id(10L).name("test").email("test@google.com").build();
        Item item = Item.builder().id(1L).name("testName").available(false).build();
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .booker(user).item(item)
                .build();
        BookingDto bookingDto = BookingMapper.mapToDto(booking,item);
        assertEquals(booking.getId(),bookingDto.getId());
        assertEquals(booking.getItem().getId(),bookingDto.getItem().getId());

    }

    @Test
    void testBookingInItemDto() {
        User user = User.builder().id(10L).name("test").email("test@google.com").build();
        Item item = Item.builder().id(1L).name("testName").available(false).build();
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .booker(user).item(item)
                .build();
        BookingDtoItem bookingDtoItem =BookingMapper.bookingInItemDto(booking);
        assertEquals(booking.getId(),bookingDtoItem.getId());
        assertEquals(booking.getBooker().getId(),bookingDtoItem.getBookerId());
    }
}