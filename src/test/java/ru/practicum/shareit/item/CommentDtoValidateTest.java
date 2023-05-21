package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;

class CommentDtoValidateTest {
    @Test
    void testCommentDto(String text) {
        final CommentDto commentDto = CommentDto.builder()
                .text(text)
                .build();

    }
}