package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.storage.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentMapper;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;
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

class ItemServiceImplTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    UserService userService;
    @Mock
    ItemMapper itemMapper;
    @Mock
    UserMapper userMapper;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentMapper commentMapper;
    ItemService itemService;

    @BeforeEach
    void beforeEach() {
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        userService = Mockito.mock(UserService.class);
        userRepository = Mockito.mock(UserRepository.class);
        itemMapper = new ItemMapper(itemRequestRepository);
        userMapper = Mappers.getMapper(UserMapper.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        commentMapper = Mockito.mock(CommentMapper.class);
        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                itemMapper,
                bookingRepository,
                commentRepository,
                commentMapper);
    }

    @Test
    void save() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(false)
                .build();

        when(itemRepository.save(any())).thenReturn(itemMapper.toItemModel(itemDto, user));
        when(userService.findUserById(1L)).thenReturn(userMapper.toUserDto(user));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        ItemDto itemDtoTest = itemService.createItem(itemDto, user.getId());

        assertEquals(itemDto.getName(), itemDtoTest.getName());
        assertEquals(itemDto.getAvailable(), itemDtoTest.getAvailable());
        assertEquals(itemDto.getDescription(), itemDtoTest.getDescription());
    }

    @Test
    void update() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();
        ItemDto itemDtoUpd = ItemDto.builder()
                .id(1L)
                .name("itemNameUpd")
                .description("itemDescriptionUpd")
                .available(true)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(itemMapper.toItemModel(itemDtoUpd, user));

        assertThrows(UserNotFoundException.class,
                () -> itemService.updateItem(itemDtoUpd, item.getId(), user.getId()));


    }

    @Test
    void findById() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(anyLong())).thenReturn(new ArrayList<>());
        ItemDto itemDtoEnhanced = itemService.getItemById(item.getId(), user.getId());

        assertEquals(item.getId(), itemDtoEnhanced.getId());
        assertEquals(item.getName(), itemDtoEnhanced.getName());
        assertEquals(item.getAvailable(), itemDtoEnhanced.getAvailable());
        assertEquals(item.getDescription(), itemDtoEnhanced.getDescription());
    }

    @Test
    void testGetAllItems() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();

        when(itemRepository.findAllByOwner(any())).thenReturn(List.of(item));
        when(userService.findUserById(1L)).thenReturn(userMapper.toUserDto(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        List<ItemDto> ideList = itemService.getAllItems(user.getId());

        assertEquals(1, ideList.size());
        assertEquals(item.getId(), ideList.get(0).getId());
        assertEquals(item.getName(), ideList.get(0).getName());
        assertEquals(item.getAvailable(), ideList.get(0).getAvailable());
        assertEquals(item.getDescription(), ideList.get(0).getDescription());
    }

    @Test
    void saveComment() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(userOne)
                .build();
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Booking booking = new Booking(1L, LocalDateTime.now().plusMinutes(8),
                LocalDateTime.now().minusMinutes(16), item, userTwo, BookingStatus.APPROVED);
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .text("comment info")
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("comment info")
                .author(userTwo)
                .created(LocalDateTime.now())
                .build();

        when(userService.findUserById(1L)).thenReturn(userMapper.toUserDto(userTwo));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(commentRepository.save(any())).thenReturn(comment);
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any())).thenReturn(Optional.of(booking));
        CommentDto commentDtoTest = itemService.addComment(item.getId(), userTwo.getId(), commentDtoRequest);

        assertEquals(commentDtoTest.getText(), commentDtoRequest.getText());
    }
}