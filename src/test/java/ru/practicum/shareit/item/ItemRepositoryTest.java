package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Item item;
    private User user;

    @BeforeEach
    void createItem() {
        user = new User(1L, "User One", "one@gmail.com");
        item = new Item(1L, "Item One", "item 1", true, user, null);
    }

    @Test
    void findByOwner() {
        User user = new User(1L, "User One", "one@gmail.com");
        item = new Item(1L, "Item One", "item 1", true, user, null);
        User newUser = userRepository.save(user);
        item.setOwner(newUser);
        Item newItem = itemRepository.save(item);

        List<Item> items = itemRepository.findAllByOwner(newUser);
        assertThat(items).hasSize(1).contains(newItem);
    }

    @Test
    void search() {
        User user = new User(1L, "User One", "one@gmail.com");
        user = userRepository.save(user);
        item = new Item(1L, "item One", "item 1", true, user, null);
        item = itemRepository.save(item);

        List<Item> items = itemRepository.search("item");
        assertThat(items).hasSize(1).contains(item);
    }
}