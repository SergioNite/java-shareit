package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    public static final String USER_AUTH_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(USER_AUTH_HEADER) Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PostMapping(path = "/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDtoRequest commentDtoRequest) {

        return itemService.addComment(itemId, userId, commentDtoRequest);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId,
                               @RequestHeader(USER_AUTH_HEADER) Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> findAllItems(@RequestHeader(USER_AUTH_HEADER) Long userId) {
        return itemService.getAllItems(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader(USER_AUTH_HEADER) Long userId) {

        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsBySearch(@RequestParam String text) {
        return itemService.getItemsBySearch(text);
    }
}
