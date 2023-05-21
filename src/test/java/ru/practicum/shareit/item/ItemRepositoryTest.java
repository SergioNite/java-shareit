package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
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
    public void addUsers() {

        user = userRepository.save(User.builder()
                .name("user one")
                .email("one@gmail.com")
                .build());
        item = itemRepository.save(Item.builder()
                .name("item one")
                .description("item 1")
                .available(true)
                .owner(user)
                .build());
    }

    @AfterEach
    public void deleteUsers() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void testFindAllByOwner() {
        List<Item> items = itemRepository.findAllByOwner(user);
        assertThat(items).hasSize(1).contains(item);
    }

    @Test
    void testSearch() {
        List<Item> items = itemRepository.search("item");
        assertThat(items).hasSize(1);
    }


}