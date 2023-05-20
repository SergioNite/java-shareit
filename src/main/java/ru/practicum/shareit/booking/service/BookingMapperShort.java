package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapperShort {
    public static BookingDtoItem bookingToBookingDtoShort(Booking booking) {
        return BookingDtoItem.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
