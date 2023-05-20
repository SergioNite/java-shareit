package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, BookingDtoRequest bookingDtoRequest);

    BookingDto patchBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> findAllByUser(Long userId, String state);

    List<BookingDto> findAllByOwner(Long userId, String state);
}
