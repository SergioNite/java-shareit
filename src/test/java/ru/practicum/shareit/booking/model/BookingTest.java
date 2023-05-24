package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.storage.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void testEquals() {
        User user = User.builder().id(10L).name("test").email("test@google.com").build();
        Item item = Item.builder().id(1L).name("testName").available(false).build();
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .id(1L).item(item).booker(user)
                .start(now).end(now).status(BookingStatus.WAITING).build();
        assertTrue(booking.equals(booking));
        assertFalse(booking.equals(null));
        assertFalse(booking.equals(item));
    }

    @Test
    void testHashCode() {
        User user = User.builder().id(10L).name("test").email("test@google.com").build();
        Item item = Item.builder().id(1L).name("testName").available(false).build();
        item.setOwner(user);
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .id(1L).item(item).booker(user)
                .start(now).end(now).status(BookingStatus.WAITING).build();
        Booking booking2 = Booking.builder()
                .id(2L).item(item).booker(user)
                .start(now).end(now).status(BookingStatus.WAITING).build();

        assertEquals(booking.hashCode(),booking.hashCode());
        assertNotEquals(booking.hashCode(),booking2.hashCode());
    }
}