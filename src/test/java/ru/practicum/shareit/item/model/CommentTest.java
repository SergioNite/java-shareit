package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void getText() {
        User user = User.builder().id(10L).name("test").email("test@google.com").build();
        Item item = Item.builder().id(1L).name("testName").available(false).owner(user).build();
        Comment comment = Comment.builder().id(1L).text("text msg").item(item).build();
        assertEquals("text msg", comment.getText());
    }

    @Test
    void getItem() {
        User user = User.builder().id(10L).name("test").email("test@google.com").build();
        Item item = Item.builder().id(1L).name("testName").available(false).owner(user).build();
        Comment comment = Comment.builder().id(1L).text("text msg").item(item).build();
        assertEquals(item, comment.getItem());
    }

    @Test
    void getId() {
        User user = User.builder().id(10L).name("test").email("test@google.com").build();
        Item item = Item.builder().id(1L).name("testName").available(false).owner(user).build();
        Comment comment = Comment.builder().id(1L).text("text msg").item(item).build();
        assertEquals(1L, comment.getId());
    }

    @Test
    void getAuthor() {
        User user = User.builder().id(10L).name("test").email("test@google.com").build();
        Item item = Item.builder().id(1L).name("testName").available(false).owner(user).build();
        Comment comment = Comment.builder().id(1L).author(user).item(item).build();
        assertEquals(user, comment.getAuthor());
    }
}