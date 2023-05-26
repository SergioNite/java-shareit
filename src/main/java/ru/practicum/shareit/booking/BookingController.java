package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private BookingService bookingService;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto addBooking(@RequestBody @Validated BookingDtoRequest bookingDtoRequest,
                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.addBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {

        return bookingService.patchBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Long bookingId,
                               @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllByUser(@RequestParam(defaultValue = "ALL") String state,
                                          @RequestHeader(USER_ID_HEADER) Long userId,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.findAllByUser(userId, state, PageRequest.of((from == 0 ? 0 : (from / size)), size));
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestParam(defaultValue = "ALL") String state,
                                           @RequestHeader(USER_ID_HEADER) Long userId,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.findAllByOwner(userId, state, PageRequest.of((from == 0 ? 0 : (from / size)), size));
    }
}
