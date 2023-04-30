package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndStatus(Long userId, BookingStatus status, Sort sort);

    List<Booking> findByBookerId(Long userId, Sort sort);

    List<Booking> findBookingByItemOwnerId(Long userId, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndStatus(Long userId, BookingStatus status, Sort sort);

    @Query("SELECT b FROM bookings b " +
            "WHERE b.item.id = ?1 " +
            "AND (b.status = 'APPROVED' OR b.status IS NULL) " +
            "AND b.end > CURRENT_TIMESTAMP " +
            "ORDER BY b.start ASC"
    )
    Collection<Booking> getActiveBookings(Long itemId);

    @Query("select b from bookings b " +
            "where b.booker.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start asc ")
    List<Booking> findByBookerIdCurrent(Long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndEndIsBefore(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long userId, LocalDateTime now, Sort sort);

    Optional<Booking> findFirstByBookerIdAndItemIdAndEndBefore(long id, long itemId, LocalDateTime now);


    @Query("select b from bookings b " +
            "where b.item.owner.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start asc")
    List<Booking> findBookingByItemOwnerIdCurrent(Long userId, LocalDateTime now);

    List<Booking> findBookingByItemOwnerIdAndEndIsBefore(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndStartIsAfter(Long userId, LocalDateTime now, Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(long itemId, BookingStatus bookingStatus, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStart(long itemId, BookingStatus bookingStatus, LocalDateTime now);


}
