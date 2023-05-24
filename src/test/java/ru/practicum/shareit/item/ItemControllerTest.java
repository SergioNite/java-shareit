package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@SpringJUnitWebConfig(ItemController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @MockBean
    UserService userService;
    @MockBean
    UserRepository userRepository;
    @MockBean
    ItemRepository itemRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemMapper itemMapper;
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("itemName")
            .description("itemDescription")
            .available(true)
            .build();
    private final ItemDto itemDtoEnhanced = ItemDto.builder()
            .id(1L)
            .name("itemName")
            .description("itemDescription")
            .available(true)
            .lastBooking(BookingDtoItem.builder()
                    .id(1L)
                    .bookerId(11L)
                    .build())
            .nextBooking(BookingDtoItem.builder()
                    .id(2L)
                    .bookerId(22L)
                    .build())
            .comments(List.of(CommentDto.builder()
                    .id(1L)
                    .text("comment info")
                    .created(LocalDateTime.now())
                    .build()))
            .build();

    @SneakyThrows
    @Test
    void createItem_whenValid_thenReturnItem() {
        when(itemService.createItem(any(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
        verify(itemService, times(1)).createItem(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void createItem_whenUserIdDoesNotExists_thenReturnNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(ItemNotFoundException.class);
        when(itemService.createItem(any(), anyLong())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 100L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void createItem_whenEmptyInput_thenReturnBadRequest() {
        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(null))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).createItem(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void updateItem_whenValid_thenReturnItem() {
        ItemDto itemDtoForUpdate = ItemDto.builder()
                .id(1L)
                .name("newItemName")
                .description("newItemDescription")
                .available(true)
                .build();

        when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(itemDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDtoForUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
        verify(itemService, times(1)).updateItem(any(), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void updateItem_whenItemIdInvalid_thenReturnNotFound() {
        ItemDto itemDtoForUpdate = ItemDto.builder()
                .id(1000L)
                .name("newItemName")
                .description("newItemDescription")
                .available(true)
                .build();

        when(itemRepository.findById(anyLong())).thenThrow(ItemNotFoundException.class);
        when(itemService.updateItem(any(), anyLong(), anyLong())).thenThrow(ItemNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1000")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDtoForUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(itemRepository, never()).save(any());
    }


    @SneakyThrows
    @Test
    void getItemById_whenValid_thenReturnItem() {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDtoEnhanced);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.lastBooking.id",
                        is(itemDtoEnhanced.getLastBooking().getId().intValue())))
                .andExpect(jsonPath("$.lastBooking.bookerId",
                        is(itemDtoEnhanced.getLastBooking().getBookerId().intValue())))
                .andExpect(jsonPath("$.nextBooking.id",
                        is(itemDtoEnhanced.getNextBooking().getId().intValue())))
                .andExpect(jsonPath("$.nextBooking.bookerId",
                        is(itemDtoEnhanced.getNextBooking().getBookerId().intValue())))
                .andExpect(jsonPath("$.comments[0].id",
                        is(itemDtoEnhanced.getComments().get(0).getId().intValue())))
                .andExpect(jsonPath("$.comments[0].text",
                        is(itemDtoEnhanced.getComments().get(0).getText())));
        verify(itemService, times(1)).getItemById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getItemById_whenUserIdDoesNotExists_thenReturnUserNotFoundException() {
        Mockito.when(userService.getUserById(anyLong())).thenThrow(UserNotFoundException.class);
        Mockito.when(itemService.getItemById(anyLong(),anyLong())).thenThrow(ItemNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/items/", 1L)
                        .header("X-Sharer-User-Id", 1000L))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findAllItems_whenValid_thenReturnItems() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("newItemName")
                .description("newItemDescription")
                .available(true)
                .build();
        List<ItemDto> itemDtoList = new ArrayList<>();
        itemDtoList.add(itemDto);
        Mockito.when(itemService.getAllItems(anyLong())).thenReturn(itemDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get("/items/", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }


    @SneakyThrows
    @Test
    void findItemsBySearch_whenExists_thenReturnItems() {
        when(itemService.getItemsBySearch(anyString())).thenReturn(List.of(itemDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", "text")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void addComment_whenValid_thenReturnComment() {
        CommentDto commentDto = CommentDto.builder()
                .text("commentText")
                .build();
        CommentDto commentDtoTest = CommentDto.builder()
                .id(1L)
                .created(LocalDateTime.of(2022, 2, 2, 2, 2, 2))
                .authorName("userName")
                .build();
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDtoTest);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoTest.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoTest.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDtoTest.getAuthorName()), String.class))
                .andExpect(jsonPath("$.created",
                        is(commentDtoTest.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));

        verify(itemService, times(1)).addComment(anyLong(), anyLong(), any());
    }
}