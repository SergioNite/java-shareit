package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper mapper;
    public static final String USER_AUTH_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @NotNull(message = ("Заголовок пользователя не может быть пустым"))
                              @Min(1)
                              @RequestHeader(USER_AUTH_HEADER) Long userId) {
        Item item = mapper.toItemModel(itemDto, userId);
        return mapper.toDtoItem(itemService.createItem(item));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@NotNull(message = "Предмет не может быть пустым")
                                @Min(1)
                                @PathVariable Long itemId) {
        return mapper.toDtoItem(itemService.getItemById(itemId));
    }

    @GetMapping
    public List<ItemDto> findAllItems(@NotNull(message = "Заголовок пользователя не может быть пустым")
                                      @Min(1)
                                      @RequestHeader(USER_AUTH_HEADER) Long userId) {
        List<Item> userItems = itemService.getAllItems(userId);
        return mapper.mapItemListToDto(userItems);
    }
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @NotNull(message = "Предмет не может быть пустым")
                              @Min(1)
                              @PathVariable Long itemId,
                              @NotNull(message = "Заголокок пользователя не должен быть пустым")
                              @Min(1)
                              @RequestHeader(USER_AUTH_HEADER) Long userId) {
        Item item = mapper.toItemModel(itemDto, userId);
        item.setId(itemId);
        return mapper.toDtoItem(itemService.updateItem(item));
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsByRequest(@RequestParam String text) {
        List<Item> foundItems = itemService.getItemsBySearch(text);
        return mapper.mapItemListToDto(foundItems);
    }
}
