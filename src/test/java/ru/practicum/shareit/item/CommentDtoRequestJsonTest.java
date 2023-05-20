package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDtoRequest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentDtoRequestJsonTest {
    @Autowired
    private JacksonTester<CommentDtoRequest> json;

    @SneakyThrows
    @Test
    void testCommentDtoRequest() {

        CommentDtoRequest commentDto = CommentDtoRequest.builder()
                .text("User comment text")
                .build();

        JsonContent<CommentDtoRequest> result = json.write(commentDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
    }
}