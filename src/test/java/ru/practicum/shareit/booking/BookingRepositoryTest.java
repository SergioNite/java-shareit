package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.storage.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User owner;
    private User booker;
    private Booking booking;
    private Item item;
    private final LocalDateTime start = LocalDateTime.parse("2023-05-01T01:00");
    private final LocalDateTime end = LocalDateTime.parse("2023-05-02T01:00");

    @BeforeEach
    void create() {
        owner = new User(1L, "owner", "owner@gmail.com");
        booker = new User(2L, "booker", "boker@gmail.com");
        item = new Item(1L, "item", "item description", true, owner, null);
        booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                booker.getId(), BookingStatus.WAITING, PageRequest.of(0, 2));
        assertThat(bookings).hasSize(1).contains(booking);
    }


}