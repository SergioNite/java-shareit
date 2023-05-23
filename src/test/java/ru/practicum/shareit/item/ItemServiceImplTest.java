package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.storage.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotAvailibleException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentMapper;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.exceptions.EmailErrorException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    void createItem_whenValidInput_thenReturnCreatedItem() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(false)
                .build();

        when(itemRepository.save(any())).thenReturn(itemMapper.toItemModel(itemDto, user));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        ItemDto itemDtoTest = itemService.createItem(itemDto, user.getId());

        assertEquals(itemDto.getName(), itemDtoTest.getName());
        assertEquals(itemDto.getAvailable(), itemDtoTest.getAvailable());
        assertEquals(itemDto.getDescription(), itemDtoTest.getDescription());
    }

    @Test
    void createItem_whenUserIdInvalid_thenThrowException() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .build();

        assertThrows(ItemNotFoundException.class, () -> itemService.createItem(itemDto, -1L));

    }

    @Test
    void createItem_whenAvailableInvalid_thenThrowException() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(null)
                .build();

        assertThrows(ItemNotAvailibleException.class, () -> itemService.createItem(itemDto, user.getId()));

    }

    @Test
    void createItem_whenNameInvalid_thenThrowException() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name(null)
                .description("itemDescription")
                .available(true)
                .build();

        assertThrows(ItemNotAvailibleException.class, () -> itemService.createItem(itemDto, user.getId()));

    }

    @Test
    void createItem_whenNameEmpty_thenThrowException() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("")
                .description("itemDescription")
                .available(true)
                .build();

        assertThrows(ItemNotAvailibleException.class, () -> itemService.createItem(itemDto, user.getId()));

    }

    @Test
    void createItem_whenDescriptionEmpty_thenThrowException() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("test")
                .description("")
                .available(true)
                .build();

        assertThrows(ItemNotAvailibleException.class, () -> itemService.createItem(itemDto, user.getId()));

    }

    @Test
    void createItem_whenDescriptionInvalid_thenThrowException() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("test")
                .description(null)
                .available(true)
                .build();

        assertThrows(ItemNotAvailibleException.class, () -> itemService.createItem(itemDto, user.getId()));

    }

    @Test
    void createItem_whenRepositoryError_thenThrowException() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("test")
                .description("test")
                .available(true)
                .build();

        when(itemRepository.save(any())).thenReturn(itemMapper.toItemModel(itemDto, user));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenThrow(ItemNotAvailibleException.class);

        assertThrows(ItemNotAvailibleException.class, () -> itemService.createItem(itemDto, user.getId()));

    }


    @Test
    void updateItem_whenInputValid_thenReturnUpdatedItem() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        User user2 = new User(1L, "testName", "testEmail@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user2)
                .build();
        ItemDto itemDtoUpd = ItemDto.builder()
                .id(1L)
                .name("itemNameUpd")
                .description("itemDescriptionUpd")
                .available(false)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(itemMapper.toItemModel(itemDtoUpd, user));

        ItemDto resultItemDto = itemService.updateItem(itemDtoUpd, item.getId(), user.getId());

        assertEquals(resultItemDto.getName(), itemDtoUpd.getName());
        assertEquals(resultItemDto.getDescription(), itemDtoUpd.getDescription());
        assertEquals(resultItemDto.getAvailable(), itemDtoUpd.getAvailable());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void updateItem_whenUserInvalid_thenThrowException() {
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

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        assertThrows(UserNotFoundException.class,
                () -> itemService.updateItem(itemDtoUpd, item.getId(), user.getId()));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItemById_whenInputValid_thenReturnItem() {
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
    void getItemById_whenItemInvalid_thenThrowException() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(item.getId(), user.getId()));
    }

    @Test
    void getItemById_whenOwnerEqualsUser_thenReturnItemDtoWithBooking() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        User booker = new User(2L, "BookerName", "BookerEmail@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();
        Booking lastBooking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                anyLong(), any(), any())).thenReturn(Optional.ofNullable(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStart(
                anyLong(), any(), any())).thenReturn(Optional.ofNullable(lastBooking));
        ItemDto itemDtoEnhanced = itemService.getItemById(item.getId(), user.getId());
        assertEquals(itemDtoEnhanced.getId(), item.getId());
        assertNotNull(itemDtoEnhanced.getLastBooking());
        assertNotNull(itemDtoEnhanced.getNextBooking());
    }

    @Test
    void getAllItems_whenValidInput_thenReturnItemList() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();

        when(itemRepository.findAllByOwner(any())).thenReturn(List.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        List<ItemDto> ideList = itemService.getAllItems(user.getId());

        assertEquals(1, ideList.size());
        assertEquals(item.getId(), ideList.get(0).getId());
        assertEquals(item.getName(), ideList.get(0).getName());
        assertEquals(item.getAvailable(), ideList.get(0).getAvailable());
        assertEquals(item.getDescription(), ideList.get(0).getDescription());
    }

    @Test
    void getAllItems_whenUserOwnerInvalid_thenThrowException() {
        User user = new User(1L, "testName", "testEmail@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.getAllItems(user.getId()));
    }

    @Test
    void addComment_whenValidInput_thenSaveAndReturnComment() {
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

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(commentRepository.save(any())).thenReturn(comment);
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any())).thenReturn(Optional.of(booking));

        CommentDto commentDtoTest = itemService.addComment(item.getId(), userTwo.getId(), commentDtoRequest);

        assertEquals(commentDtoTest.getText(), commentDtoRequest.getText());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void addComment_whenUserInvalid_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(userOne)
                .build();
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .text("comment info")
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.addComment(item.getId(), userTwo.getId(), commentDtoRequest));
    }

    @Test
    void addComment_whenBookingEmpty_thenThrowException() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(userOne)
                .build();
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .text("comment info")
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any())).thenReturn(Optional.empty());

        assertThrows(EmailErrorException.class, () -> itemService.addComment(item.getId(), userTwo.getId(), commentDtoRequest));
    }

    @Test
    void addComment_whenItemInvalid_thenThrowException() {
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

        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.addComment(item.getId(), userTwo.getId(), commentDtoRequest));
    }
}