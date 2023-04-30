package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.exceptions.DateConflictException;
import ru.practicum.shareit.booking.exceptions.UnavaibleDatePeriodException;
import ru.practicum.shareit.booking.exceptions.UnavailableBookingException;
import ru.practicum.shareit.booking.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.storage.BookingState;
import ru.practicum.shareit.booking.storage.BookingStatus;
import ru.practicum.shareit.item.exceptions.ItemAccessDeniedException;
import ru.practicum.shareit.item.exceptions.ItemNotAvailibleException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.storage.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.storage.BookingStatus.WAITING;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto addBooking(Long userId, BookingDtoRequest bookingDtoRequest) {

        LocalDateTime bookingStart = bookingDtoRequest.getStart();
        LocalDateTime bookingEnd = bookingDtoRequest.getEnd();
        if (bookingEnd.isBefore(bookingStart) || bookingEnd.equals(bookingStart)) {
            throw new UnavaibleDatePeriodException("Некорректные даты бронирования");
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ItemNotFoundException("Пользователь не найден " + userId)
        );
        Item item = itemRepository.findById(bookingDtoRequest.getItemId()).orElseThrow(
                () -> new ItemNotFoundException("Предмет не найден " + bookingDtoRequest.getItemId())
        );

        if (userId.equals(item.getOwner().getId())) {
            throw new UnavailableBookingException("Владельцу запрещено бронировать свой предмет " + item.getId());
        }

        if (!item.getAvailable()) {
            throw new ItemNotAvailibleException("Нельзя забронировать недоступный предмет " + item.getId());
        }

        Booking booking = bookingMapper.mapToModel(bookingDtoRequest, item, user);
        booking.setStatus(WAITING);
        if (itemRepository.existsItemByIdAndAvailableIsTrue(item.getId())) {
            if (isBookingAvailable(booking)) {
                booking = bookingRepository.save(booking);
                return BookingMapper.mapToDto(booking, item);
            } else
                throw new DateConflictException(
                        String.format("Некорректный период %s по %s", booking.getStart(), booking.getEnd()));
        } else throw new ItemNotAvailibleException(String.format("Предмет %d недоступен.", item.getId()));

    }

    private boolean isBookingAvailable(Booking booking) {
        List<Booking> activeBookings = new ArrayList<>(
                bookingRepository.getActiveBookings(booking.getItem().getId()));
        final LocalDateTime startTime = booking.getStart();
        final LocalDateTime endTime = booking.getEnd();
        LocalDateTime leftBorder = LocalDateTime.now();
        LocalDateTime rightBorder;

        if (activeBookings.size() > 0) {
            for (Booking activeBooking : activeBookings) {
                rightBorder = activeBooking.getStart();

                if (leftBorder.isBefore(startTime) && rightBorder.isAfter(endTime)) {
                    return true;

                } else if (leftBorder.isAfter(endTime)) {
                    return false;
                }
                leftBorder = rightBorder;
            }
            return leftBorder.isBefore(startTime);

        } else return true;
    }

    @Override
    @Transactional
    public BookingDto patchBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ItemNotFoundException("Запрос не найден " + bookingId)
        );
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(
                () -> new ItemNotFoundException("Предмет не найден")
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ItemNotFoundException("Пользователь не найден " + userId)
        );
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new UserNotFoundException("Доступ запрещен!");
        }
        BookingStatus status = convertBooleanToStatusEnum(approved);

        if (booking.getStatus().equals(status)) {
            throw new IllegalArgumentException("Статус был установлен!");
        }

        booking.setStatus(status);
        booking = bookingRepository.save(booking);
        return BookingMapper.mapToDto(booking, item);
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден " + userId)
        );
        if (Objects.isNull(user)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ItemNotFoundException("Запрос не найден " + bookingId)
        );
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner().getId()) {
            throw new UserNotFoundException("Доступ запрещен");
        }
        return BookingMapper.mapToDto(booking, booking.getItem());
    }

    @Override
    public List<BookingDto> findAllByUser(Long userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден " + userId));
        BookingState bookingState = parseBookingState(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        Sort sort = Sort.by("start").descending();
        switch (bookingState) {
            case REJECTED :
                bookings = bookingRepository.findByBookerIdAndStatus(user.getId(), REJECTED, sort);
                break;
            case WAITING :
                bookings = bookingRepository.findByBookerIdAndStatus(user.getId(), WAITING, sort);
                break;
            case CURRENT :
                bookings = bookingRepository.findByBookerIdCurrent(user.getId(), now);
                break;
            case FUTURE :
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(user.getId(), now, sort);
                break;
            case PAST :
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(user.getId(), now, sort);
                break;
            case ALL :
                bookings = bookingRepository.findByBookerId(user.getId(), sort);
                break;
            default :
                throw new IllegalArgumentException("Unknown state: Error");
        }
        return bookings.stream()
                .map(booking -> bookingMapper.mapToDto(booking, booking.getItem()))
                .collect(Collectors.toCollection(ArrayList::new));

    }

    @Override
    public List<BookingDto> findAllByOwner(Long userId, String state) {
        List<Booking> bookings;
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден " + userId));

        BookingState bookingState = parseBookingState(state);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();

        switch (bookingState) {
            case REJECTED :
                bookings = bookingRepository.findBookingByItemOwnerIdAndStatus(user.getId(), REJECTED, sort);
                break;
            case WAITING :
                bookings = bookingRepository.findBookingByItemOwnerIdAndStatus(user.getId(), WAITING, sort);
                break;
            case CURRENT :
                bookings = bookingRepository.findBookingByItemOwnerIdCurrent(user.getId(), now);
                break;
            case FUTURE :
                bookings = bookingRepository.findBookingByItemOwnerIdAndStartIsAfter(user.getId(), now, sort);
                break;
            case PAST :
                bookings = bookingRepository.findBookingByItemOwnerIdAndEndIsBefore(user.getId(), now, sort);
                break;
            case ALL :
                bookings = bookingRepository.findBookingByItemOwnerId(user.getId(), sort);
                break;
            default :
                throw new IllegalArgumentException("Unknown state: Error");
        }
        return bookings.stream()
                .map(booking -> bookingMapper.mapToDto(booking, booking.getItem()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private BookingStatus convertBooleanToStatusEnum(Boolean approved) {
        return approved ? BookingStatus.APPROVED : REJECTED;
    }

    private BookingState parseBookingState(String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return bookingState;
    }

}
