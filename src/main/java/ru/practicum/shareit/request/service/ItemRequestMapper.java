package ru.practicum.shareit.request.service;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequest itemRequestDtoToItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto itemRequestToItemRequestDto(ItemRequest itemRequest);
}
