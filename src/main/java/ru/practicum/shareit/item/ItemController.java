package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;
    public static final String USER_AUTH_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestBody @Valid ItemDto itemDto,
                              @NotNull(message = ("Заголовок пользователя не может быть пустым"))
                              @Min(1)
                              @RequestHeader(USER_AUTH_HEADER) Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PostMapping(path = "/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody @Validated CommentDtoRequest commentDtoRequest) {

        return itemService.addComment(itemId, userId, commentDtoRequest);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@NotNull(message = "Предмет не может быть пустым")
                               @Min(1)
                               @PathVariable Long itemId,
                               @RequestHeader(USER_AUTH_HEADER) Long userId) {
        return itemService.getItemById(itemId,userId);
    }

    @GetMapping
    public List<ItemDto> findAllItems(@NotNull(message = "Заголовок пользователя не может быть пустым")
                                      @Min(1)
                                      @RequestHeader(USER_AUTH_HEADER) Long userId) {
        return itemService.getAllItems(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody @Valid ItemDto itemDto,
                              @NotNull(message = "Предмет не может быть пустым")
                              @Min(1)
                              @PathVariable Long itemId,
                              @NotNull(message = "Заголокок пользователя не должен быть пустым")
                              @Min(1)
                              @RequestHeader(USER_AUTH_HEADER) Long userId) {

        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsByRequest(@RequestParam String text) {
        return itemService.getItemsBySearch(text);
    }
}
