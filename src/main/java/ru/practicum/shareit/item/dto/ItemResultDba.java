package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ItemResultDba {
    @Id
    @Column(name = "item_id")
    private Long itemId;
    private String name;
    private String description;
    private Boolean available;
    @Column(name = "owner_id")
    private Long ownerId;
    @Column(name = "next_booking_id")
    private Long nextBookingId;
    @Column(name = "next_start_time")
    private LocalDateTime nextStartTime;
    @Column(name = "next_end_time")
    private LocalDateTime nextEndTime;
    @Column(name = "next_booker_id")
    private Long nextBookerId;
    @Column(name = "last_booking_id")
    private Long lastBookingId;
    @Column(name = "last_start_time")
    private LocalDateTime lastStartTime;
    @Column(name = "last_end_time")
    private LocalDateTime lastEndTime;
    @Column(name = "last_booker_id")
    private Long lastBookerId;

}
