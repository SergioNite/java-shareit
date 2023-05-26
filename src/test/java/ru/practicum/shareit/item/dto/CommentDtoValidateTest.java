package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentDtoValidateTest {
    @Test
    void testCommentDto() {
        LocalDateTime now = LocalDateTime.now();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("test")
                .authorName("MyName")
                .created(now).build();
        assertEquals(1L, commentDto.getId());
        assertEquals("test", commentDto.getText());
        assertEquals("MyName", commentDto.getAuthorName());
        assertEquals(now, commentDto.getCreated());

    }
}