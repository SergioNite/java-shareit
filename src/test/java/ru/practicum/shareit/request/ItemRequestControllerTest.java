package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    private final User user = new User(1L, "testName", "testEmail@gmail.com");
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("itemName")
            .description("itemDescription")
            .available(true)
            .requestId(1L)
            .build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("test description")
            .created(LocalDateTime.now())
            .requester(user)
            .items(List.of(itemDto))
            .build();

    @SneakyThrows
    @Test
    void addRequest_whenValid_thenReturnRequest() {
        when(itemRequestService.addRequest(any(), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
        verify(itemRequestService, times(1)).addRequest(any(ItemRequestDto.class), anyLong());

    }

    @SneakyThrows
    @Test
    void addRequest_whenUserIdInvalid_thenReturnException() {
        when(itemRequestService.addRequest(any(), anyLong())).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", -1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addRequest_whenInvalidDescription_thenReturnException() {
        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .description("")
                .build();

        when(itemRequestService.addRequest(any(), anyLong())).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findByOwnerId_whenValid_thenReturnRequestList() {
        when(itemRequestService.findByOwnerId(anyLong())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));
    }

    @SneakyThrows
    @Test
    void findByOwnerId_whenUserIdNotFound_thenReturnException() {
        when(itemRequestService.findByOwnerId(anyLong())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", 1000L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void findAll_whenUserIdIsValid_thenReturnAllRequestList() {
        when(itemRequestService.findAll(anyLong(), any())).thenReturn(List.of(itemRequestDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "4")
                        .param("from", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));
    }

    @SneakyThrows
    @Test
    void findAll_whenUserIdIsInvalid_thenReturnNotFoundException() {
        when(itemRequestService.findAll(anyLong(), any())).thenThrow(UserNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "4")
                        .param("from", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void findAll_whenPageSizeChanged_returnAllRequestList() {
        when(itemRequestService.findAll(anyLong(), any())).thenReturn(List.of(itemRequestDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "4")
                        .param("from", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));
    }

    @SneakyThrows
    @Test
    void findById_whenValid_thenReturn() {
        when(itemRequestService.findById(anyLong(), anyLong())).thenReturn(itemRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }

    @SneakyThrows
    @Test
    void findById_whenRequestIdInvalid_thenReturnException() {
        when(itemRequestService.findById(anyLong(), anyLong())).thenThrow(ItemNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/100")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}