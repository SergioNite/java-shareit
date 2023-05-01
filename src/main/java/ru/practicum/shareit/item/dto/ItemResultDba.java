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
    private Long item_id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner_id;
    private Long next_booking_id;
    private LocalDateTime next_start_time;
    private LocalDateTime next_end_time;
    private Long next_booker_id;
    private Long last_booking_id;
    private LocalDateTime last_start_time;
    private LocalDateTime last_end_time;
    private Long last_booker_id;

}
