package ru.practicum.shareit.booking.service;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Service
@Mapper(componentModel = "spring")
public class BookingMapper {
    public static Booking mapToModel(BookingDtoRequest bookingDtoRequest, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoRequest.getStart());
        booking.setEnd(bookingDtoRequest.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingDto mapToDto(Booking booking, Item item) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setItem(item);
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static BookingDtoItem bookingInItemDto(Booking booking) {
        if (booking == null) return null;

        BookingDtoItem dto = new BookingDtoItem();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        return dto;
    }


}
