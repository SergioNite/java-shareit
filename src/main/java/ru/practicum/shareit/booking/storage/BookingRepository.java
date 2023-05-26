package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM bookings b " +
            "WHERE b.item.id = ?1 " +
            "AND (b.status = 'APPROVED' OR b.status IS NULL) " +
            "AND b.end > CURRENT_TIMESTAMP " +
            "ORDER BY b.start ASC"
    )
    Collection<Booking> getActiveBookings(Long itemId);

    Optional<Booking> findFirstByBookerIdAndItemIdAndEndBefore(long id, long itemId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
            long itemId, BookingStatus bookingStatus, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStart(
            long itemId, BookingStatus bookingStatus, LocalDateTime now);

    List<Booking> findAllByItemInAndStartLessThanEqualAndStatusIsOrderByStartDesc(
            List<Item> items, LocalDateTime now, BookingStatus status);

    List<Booking> findAllByItemInAndStartAfterAndStatusIsOrderByStartAsc(
            List<Item> items, LocalDateTime now, BookingStatus status);

}
