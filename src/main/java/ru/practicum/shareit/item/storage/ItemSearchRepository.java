package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemResultDba;

import java.util.List;

@Transactional
@Repository
public interface ItemSearchRepository extends JpaRepository<ItemResultDba, Long> {

    @Query(value = "select it.item_id as item_id," +
            "it.name as name," +
            "it.description as description," +
            "it.available as available," +
            "it.owner_id as owner_id," +
            "tbl_NextBooking.booking_id as next_booking_id," +
            "tbl_NextBooking.start_time as next_start_time," +
            "tbl_NextBooking.end_time as next_end_time," +
            "tbl_NextBooking.booker_id as next_booker_id," +
            "tbl_LastBooking.booking_id as last_booking_id," +
            "tbl_LastBooking.start_time as last_start_time," +
            "tbl_LastBooking.end_time as last_end_time," +
            "tbl_LastBooking.booker_id as last_booker_id " +
            "from items it " +
            "left outer join (" +
            "select item_id,max(start_time) as next_start_time " +
            "from bookings bn " +
            "where bn.status = 'APPROVED' and " +
            "bn.item_id IN " +
            "(select distinct it.item_id from items it where owner_id=:owner_id) " +
            "and start_time<CURRENT_TIMESTAMP " +
            "group by bn.item_id,bn.status) tbl_tempNext " +
            "on it.item_id = tbl_tempNext.item_id " +
            "left outer join (" +
            "select * from bookings where status='APPROVED'" +
            ") tbl_NextBooking " +
            "on tbl_NextBooking.item_id = tbl_tempNext.item_id " +
            "and tbl_NextBooking.start_time = tbl_tempNext.next_start_time " +
            "left outer join (" +
            "select item_id,max(start_time) as last_start_time " +
            "from bookings bn " +
            "where bn.status = 'APPROVED' and " +
            "bn.item_id IN " +
            "(select distinct it.item_id from items it where owner_id=:owner_id) " +
            "and start_time>CURRENT_TIMESTAMP " +
            "group by bn.item_id,bn.status) as tbl_tempLast " +
            "on it.item_id = tbl_tempLast.item_id " +
            "left outer join (" +
            "select * from bookings where status='APPROVED'" +
            ") tbl_LastBooking " +
            "on tbl_LastBooking.item_id = tbl_tempLast.item_id " +
            "and tbl_LastBooking.start_time = tbl_tempLast.last_start_time " +
            "where it.owner_id = :owner_id order by it.item_id",
            nativeQuery = true)
    List<ItemResultDba> findAllItemByOwnerID(@Param("owner_id") long owner_id);
}
