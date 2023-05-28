package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank(groups = Create.class, message = "Name should not be empty")
    private String name;
    @NotBlank(groups = Create.class, message = "Item description should not be empty")
    private String description;
    @NotNull(groups = Create.class, message = "Item available should not be empty")
    private Boolean available;
    private Long requestId;

    public ItemDto(String name, String description, Boolean available, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
