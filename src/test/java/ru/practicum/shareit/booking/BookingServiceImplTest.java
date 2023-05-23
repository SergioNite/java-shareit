package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.exceptions.DateConflictException;
import ru.practicum.shareit.booking.exceptions.UnavaibleDatePeriodException;
import ru.practicum.shareit.booking.exceptions.UnavailableBookingException;
import ru.practicum.shareit.booking.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.storage.BookingStatus;
import ru.practicum.shareit.item.exceptions.ItemNotAvailibleException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


class BookingServiceImplTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserService userService;
    @Mock
    UserMapper userMapper;
    @Mock
    BookingMapper bookingMapper;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    BookingService bookingService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void beforeEach() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        userService = Mockito.mock(UserService.class);
        userMapper = Mappers.getMapper(UserMapper.class);
        bookingMapper = Mappers.getMapper(BookingMapper.class);
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        bookingService = new BookingServiceImpl(
                userRepository,
                itemRepository,
                bookingRepository,
                bookingMapper
        );
    }

    @Test
    void addBooking_whenInputValid_thenSaveAndReturnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.existsItemByIdAndAvailableIsTrue(anyLong())).thenReturn(true);

        BookingDto bookingDto = bookingService.addBooking(userTwo.getId(), bookingRequest);

        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
    }

    @Test
    void addBooking_whenBookingDateIsInvalid_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(120))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().minusMinutes(90))
                .end(LocalDateTime.now().minusMinutes(60))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.existsItemByIdAndAvailableIsTrue(anyLong())).thenReturn(true);

        assertThrows(UnavaibleDatePeriodException.class, () -> bookingService.addBooking(userTwo.getId(), bookingRequest));
    }

    @Test
    void addBooking_whenUserInvalid_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().minusMinutes(90))
                .end(LocalDateTime.now().minusMinutes(60))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.empty());
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.existsItemByIdAndAvailableIsTrue(anyLong())).thenReturn(true);

        assertThrows(ItemNotFoundException.class, () -> bookingService.addBooking(userTwo.getId(), bookingRequest));
    }

    @Test
    void addBooking_whenItemInvalid_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().minusMinutes(90))
                .end(LocalDateTime.now().minusMinutes(60))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userOne));
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.existsItemByIdAndAvailableIsTrue(anyLong())).thenReturn(true);

        assertThrows(ItemNotFoundException.class, () -> bookingService.addBooking(userTwo.getId(), bookingRequest));
    }

    @Test
    void addBooking_whenUserEqualsOwner_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().minusMinutes(90))
                .end(LocalDateTime.now().minusMinutes(60))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userOne));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.existsItemByIdAndAvailableIsTrue(anyLong())).thenReturn(true);

        assertThrows(UnavailableBookingException.class, () -> bookingService.addBooking(userOne.getId(), bookingRequest));
    }

    @Test
    void addBooking_whenItemIsNotAvailable_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", false, userOne, null);
        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().minusMinutes(90))
                .end(LocalDateTime.now().minusMinutes(60))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userOne));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.existsItemByIdAndAvailableIsTrue(anyLong())).thenReturn(true);

        assertThrows(ItemNotAvailibleException.class, () -> bookingService.addBooking(userTwo.getId(), bookingRequest));
    }

    @Test
    void addBooking_whenItemIsNotExistAndAvailableInRepository_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().minusMinutes(90))
                .end(LocalDateTime.now().minusMinutes(60))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userOne));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.existsItemByIdAndAvailableIsTrue(anyLong())).thenReturn(false);

        assertThrows(ItemNotAvailibleException.class, () -> bookingService.addBooking(userTwo.getId(), bookingRequest));
    }

    @Test
    void addBooking_whenDateConflict_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusMinutes(80))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().minusMinutes(90))
                .end(LocalDateTime.now().minusMinutes(60))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();
        Booking booking2 = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().minusMinutes(80))
                .end(LocalDateTime.now().minusMinutes(50))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userOne));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.existsItemByIdAndAvailableIsTrue(anyLong())).thenReturn(true);
        when(bookingRepository.getActiveBookings(anyLong())).thenReturn(List.of(booking, booking2));

        assertThrows(DateConflictException.class, () -> bookingService.addBooking(userTwo.getId(), bookingRequest));
    }

    @Test
    void patchBooking_whenValidInput_thenSaveAndReturnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        BookingDto bookingDto = bookingService.patchBooking(1L, 1L, true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void patchBooking_whenBookingDoesNotExist_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.empty());
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));

        assertThrows(ItemNotFoundException.class, () -> bookingService.patchBooking(1L, 1L, true));
    }

    @Test
    void patchBooking_whenItemDoesNotExist_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));

        assertThrows(ItemNotFoundException.class, () -> bookingService.patchBooking(1L, 1L, true));
    }

    @Test
    void patchBooking_whenUserDoesNotExist_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> bookingService.patchBooking(1L, 1L, true));
    }

    @Test
    void patchBooking_whenUserNotEqualsItemOwner_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userTwo));

        assertThrows(UserNotFoundException.class, () -> bookingService.patchBooking(1L, 1L, true));
    }

    @Test
    void patchBooking_whenDoubleChangeIllegalStatus_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));

        assertThrows(IllegalArgumentException.class, () -> bookingService.patchBooking(1L, 1L, true));
    }


    @Test
    void findById_whenValidInput_thenReturnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));

        BookingDto bookingDto = bookingService.findById(userOne.getId(), booking.getId());

        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
    }


    @Test
    void findById_whenUserDoesNotExist_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.findById(userOne.getId(), booking.getId()));
    }

    @Test
    void findById_whenBookingDoesNotExist_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.empty());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));

        assertThrows(ItemNotFoundException.class, () -> bookingService.findById(userOne.getId(), booking.getId()));
    }

    @Test
    void findById_whenUserIsNotBookerAndItemOwner_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userTwo));

        assertThrows(UserNotFoundException.class, () -> bookingService.findById(userTwo.getId(), booking.getId()));
    }

    @Test
    void findAllByUser_whenValidInputAndStateAll_returnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllByUser(userOne.getId(), "ALL", Pageable.unpaged());

        assertEquals(1, result.size());
    }

    @Test
    void findAllByUser_whenValidInputAndStateRejected_returnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllByUser(userOne.getId(), "REJECTED", Pageable.unpaged());

        assertEquals(1, result.size());
    }

    @Test
    void findAllByUser_whenValidInputAndStateWaiting_returnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllByUser(userOne.getId(), "WAITING", Pageable.unpaged());

        assertEquals(result.size(), 1);
    }

    @Test
    void findAllByUser_whenValidInputAndStateCurrent_returnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(),any(),any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllByUser(userOne.getId(), "CURRENT", Pageable.unpaged());

        assertEquals(result.size(), 1);
    }

    @Test
    void findAllByUser_whenValidInputAndStateFuture_returnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(),any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllByUser(userOne.getId(), "FUTURE", Pageable.unpaged());

        assertEquals(result.size(), 1);
    }

    @Test
    void findAllByUser_whenValidInputAndStatePast_returnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(),any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllByUser(userOne.getId(), "PAST", Pageable.unpaged());

        assertEquals(result.size(), 1);
    }

    @Test
    void findAllByUser_whenUserInvalid_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,()->bookingService.findAllByUser(userOne.getId(), "PAST", Pageable.unpaged()));
    }

    @Test
    void findAllByUser_whenInvalidState_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");

        when(userRepository.findById(any())).thenReturn(Optional.of(userOne));

        assertThrows(UnsupportedStatusException.class,()->bookingService.findAllByUser(userOne.getId(), "ZZZZ", Pageable.unpaged()));
    }

    @Test
    void findAllByOwner_whenValidInputAndStateAll_returnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userOne.getId(), Pageable.unpaged())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllByOwner(userOne.getId(), "ALL", Pageable.unpaged());

        assertEquals(1,result.size());

    }

    @Test
    void findAllByOwner_whenValidInputAndStateReject_returnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),any(),any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllByOwner(userOne.getId(), "REJECTED", Pageable.unpaged());

        assertEquals(1,result.size());
    }
    @Test
    void findAllByOwner_whenValidInputAndStateWaiting_returnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),any(),any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllByOwner(userOne.getId(), "WAITING", Pageable.unpaged());

        assertEquals(1,result.size());
    }

    @Test
    void findAllByOwner_whenValidInputAndStateCurrent_returnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),any(),any(),any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllByOwner(userOne.getId(), "CURRENT", Pageable.unpaged());

        assertEquals(1,result.size());
    }

    @Test
    void findAllByOwner_whenValidInputAndStateFuture_returnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(),any(),any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllByOwner(userOne.getId(), "FUTURE", Pageable.unpaged());

        assertEquals(1,result.size());
    }

    @Test
    void findAllByOwner_whenValidInputAndStatePast_returnBooking() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(),any(),any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllByOwner(userOne.getId(), "PAST", Pageable.unpaged());

        assertEquals(1,result.size());
    }

    @Test
    void findAllByOwner_whenUserInvalid_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),any(),any())).thenReturn(List.of(booking));

        assertThrows(UserNotFoundException.class,()->bookingService.findAllByOwner(userOne.getId(), "WAITING", Pageable.unpaged()));
    }

    @Test
    void findAllByOwner_whenStateInvalid_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),any(),any())).thenReturn(List.of(booking));

        assertThrows(UnsupportedStatusException.class,()->bookingService.findAllByOwner(userOne.getId(), "zzz", Pageable.unpaged()));
    }

}