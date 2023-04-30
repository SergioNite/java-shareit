package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto item, Long itemId, Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getAllItems(Long userId);

    List<ItemDto> getItemsBySearch(String text);
    CommentDto addComment(long itemId, long authorId, CommentDtoRequest commentDto);
}
