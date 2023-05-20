package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private ItemRequest itemRequest;

    @BeforeEach
    void saveRequests() {
        user1 = new User(1L, "One", "one@gmail.com");
        user2 = new User(2L, "Two", "two@gmail.com");

        itemRequest = new ItemRequest(1L, "One", user1, LocalDateTime.now());
    }

    @Test
    void findAllByRequesterIdOrderByCreatedAsc() {
        User user1 = new User(1L, "One", "one@gmail.com");
        User requestor1 = userRepository.save(user1);
        itemRequest = new ItemRequest(1L, "One", user1, LocalDateTime.now());
        itemRequest.setRequester(requestor1);
        itemRequest = requestRepository.save(itemRequest);

        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedAsc(requestor1.getId());

        assertThat(requests).hasSize(1).contains(itemRequest);

    }

    @Test
    void findAllByRequestorNotLike() {
        User user1 = new User(1L, "One", "one@gmail.com");
        User requestor1 = userRepository.save(user1);
        itemRequest = new ItemRequest(1L, "One", user1, LocalDateTime.now());
        itemRequest.setRequester(requestor1);
        ItemRequest newItemRequest = requestRepository.save(itemRequest);
        List<ItemRequest> requests = requestRepository
                .findAllByRequesterIdNotOrderByCreatedDesc(3L, PageRequest.of(0, 2));

        assertThat(requests).hasSize(1).contains(newItemRequest);
    }
}