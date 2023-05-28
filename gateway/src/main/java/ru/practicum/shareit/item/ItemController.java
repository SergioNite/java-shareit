package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
@AllArgsConstructor
public class ItemController {
    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> addItem(@Validated(Create.class) @RequestBody ItemDto itemDto,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Item was added {}", itemDto);
        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto patchItem,
                                             @PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Item {} was changed", itemId);
        return itemClient.updateItem(itemId, userId, patchItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemEachUser(@PathVariable long itemId,
                                                  @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Get item {}", itemId);
        return itemClient.getItemEachUserById(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemOwnerUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) int from,
                                                   @RequestParam(value = "size", required = false, defaultValue = "20") @Min(1) int size) {
        log.info("Get all user's {} items", userId);
        return itemClient.getAllItemsOfOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemAvailableToRenter(@RequestParam String text,
                                                           @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) int from,
                                                           @RequestParam(value = "size", required = false, defaultValue = "20") @Min(1) int size) {
        log.info("Get available items with {}", text);
        return itemClient.getItemsAvailableToRent(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(@Validated(Create.class) @RequestBody CommentDto commentDto,
                                                   @PathVariable long itemId,
                                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Comment was added");
        return itemClient.addCommentToItem(itemId, userId, commentDto);
    }
}
