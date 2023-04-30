package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestBody @Validated BookingDtoRequest bookingDtoRequest,
                                 @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return bookingService.addBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(
            @RequestHeader(name = "X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {

        return bookingService.patchBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Long bookingId,
                               @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllByUser(@RequestParam(defaultValue = "ALL") String state,
                                          @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return bookingService.findAllByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestParam(defaultValue = "ALL") String state,
                                           @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return bookingService.findAllByOwner(userId, state);
    }
}
