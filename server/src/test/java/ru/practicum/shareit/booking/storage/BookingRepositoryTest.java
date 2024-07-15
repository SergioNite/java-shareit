package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;


    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {

        LocalDateTime start = LocalDateTime.parse("2023-05-01T01:00");
        LocalDateTime end = LocalDateTime.parse("2023-05-02T01:00");
        User owner = new User(1L, "owner", "owner@gmail.com");
        User booker = new User(2L, "booker", "boker@gmail.com");
        Item item = new Item(1L, "item", "item description", true, owner, null);
        Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);

        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                booker.getId(), BookingStatus.WAITING, PageRequest.of(0, 2));
        assertThat(bookings).hasSize(1).contains(booking);
    }


}