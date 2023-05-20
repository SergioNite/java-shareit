package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.storage.BookingStatus;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


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
    void save() {
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
    void update() {
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
    }


    @Test
    void updateThrowsNotFoundIfUserIsNotOwner() {
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .status(BookingStatus.WAITING)
                .booker(userTwo)
                .item(Item.builder()
                        .available(true)
                        .owner(userTwo)
                        .build())
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        assertThrows(ItemNotFoundException.class, () -> bookingService.patchBooking(1L, 1L, true));
    }

    @Test
    void findByIdAndUserId() {
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
    void findAllByOwnerAndStateAll() {
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

        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingService.findAllByOwner(userOne.getId(), "ALL", Pageable.unpaged())).thenReturn(Collections.emptyList());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));

        BookingDto bookingDto = bookingService.patchBooking(userOne.getId(), 1L, true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

}