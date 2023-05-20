package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemRequestMapper itemRequestMapper;
    @Mock
    ItemMapper itemMapper;
    ItemRequestService itemRequestService;


    @BeforeEach
    void beforeEach() {
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);
        itemMapper = new ItemMapper(itemRequestRepository);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository,
                userRepository,
                itemRepository,
                itemRequestMapper,
                itemMapper);
    }

    @Test
    void save() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("test description")
                .created(LocalDateTime.now())
                .requester(user)
                .build();

        itemRequestMapper.itemRequestDtoToItemRequest(null);
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ItemRequestDto itemRequestDtoTest = itemRequestService
                .addRequest(itemRequestDto, itemRequestDto.getRequester().getId());

        assertEquals(itemRequestDto.getId(), itemRequestDtoTest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequestDtoTest.getDescription());
    }

    @Test
    void saveWithWrongOwner() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("test description")
                .created(LocalDateTime.now())
                .requester(user)
                .build();

        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto));
        when(userRepository.findById(-1L)).thenReturn(Optional.of(user));

        assertThrows(UserNotFoundException.class, () -> itemRequestService
                .addRequest(itemRequestDto, itemRequestDto.getRequester().getId()));
    }

    @Test
    void findByOwnerId() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .requester(userOne)
                .build();
        Item item = new Item(1L, "itemName", "itemDescription", true, userTwo,
                itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedAsc(anyLong()))
                .thenReturn(List.of(itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto)));
        when(itemRepository.findAllByRequestIn(any())).thenReturn(List.of(item));
        List<ItemRequestDto> irdList = itemRequestService.findByOwnerId(userOne.getId());

        assertEquals(itemRequestDto.getId(), irdList.get(0).getId());
        assertEquals(itemRequestDto.getDescription(), irdList.get(0).getDescription());
        assertEquals(item.getId(), irdList.get(0).getItems().get(0).getId());
        assertEquals(item.getName(), irdList.get(0).getItems().get(0).getName());
    }

    @Test
    void findByOwnerIdWithWrongOwner() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .requester(userOne)
                .build();
        Item item = new Item(1L, "itemName", "itemDescription", true, userTwo,
                itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto));

        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedAsc(anyLong()))
                .thenReturn(List.of(itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto)));
        when(itemRepository.findAllByRequestIn(any())).thenReturn(List.of(item));
        when(userRepository.findById(-1L)).thenReturn(Optional.of(userOne));

        assertThrows(UserNotFoundException.class, () -> itemRequestService.findByOwnerId(userOne.getId()));
    }

    @Test
    void findAll() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .requester(userOne)
                .build();
        Item item = new Item(1L, "itemName", "itemDescription", true, userTwo,
                itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(any(), any()))
                .thenReturn(List.of(itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto)));
        when(itemRepository.findAllByRequestIn(any())).thenReturn(List.of(item));
        List<ItemRequestDto> irdList = itemRequestService.findAll(userOne.getId(), Pageable.unpaged());

        assertEquals(itemRequestDto.getId(), irdList.get(0).getId());
        assertEquals(itemRequestDto.getDescription(), irdList.get(0).getDescription());
        assertEquals(item.getId(), irdList.get(0).getItems().get(0).getId());
        assertEquals(item.getName(), irdList.get(0).getItems().get(0).getName());
    }

    @Test
    void findById() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .requester(userOne)
                .build();
        Item item = new Item(1L, "itemName", "itemDescription", true, userTwo,
                itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto)));
        when(itemRepository.findAllByRequest(any())).thenReturn(List.of(item));
        ItemRequestDto ird = itemRequestService.findById(userTwo.getId(), itemRequestDto.getRequester().getId());

        assertEquals(itemRequestDto.getId(), ird.getId());
        assertEquals(itemRequestDto.getDescription(), ird.getDescription());
        assertEquals(item.getId(), ird.getItems().get(0).getId());
        assertEquals(item.getName(), ird.getItems().get(0).getName());
    }

    @Test
    void findByIdWithEmptyItemList() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .requester(userOne)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto)));
        when(itemRepository.findAllByRequest(any())).thenReturn(new ArrayList<>());
        ItemRequestDto ird = itemRequestService.findById(userTwo.getId(), itemRequestDto.getRequester().getId());

        assertEquals(itemRequestDto.getId(), ird.getId());
        assertEquals(itemRequestDto.getDescription(), ird.getDescription());
        assertEquals(new ArrayList<>(), ird.getItems());
    }
}