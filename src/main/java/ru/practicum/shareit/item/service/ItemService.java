package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto item, Long itemId, Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllItems(Long userId);

    List<ItemDto> getItemsBySearch(String text);
}
