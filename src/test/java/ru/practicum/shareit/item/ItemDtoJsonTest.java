package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @SneakyThrows
    @Test
    void testItemDto() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item name")
                .description("item description")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).hasEmptyJsonPathValue("$.lastBooking");
        assertThat(result).hasEmptyJsonPathValue("$.nextBooking");
        assertThat(result).hasEmptyJsonPathValue("$.comments");
    }

    @SneakyThrows
    @Test
    void testItemDtoWithoutRequest() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item name")
                .description("item description")
                .available(true)
                .comments(Collections.emptyList())
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).hasEmptyJsonPathValue("$.lastBooking");
        assertThat(result).hasEmptyJsonPathValue("$.nextBooking");
        assertThat(result).hasEmptyJsonPathValue("$.comments");
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEmpty();
    }


}