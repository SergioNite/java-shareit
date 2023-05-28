package ru.practicum.shareit.request.service;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@NoArgsConstructor
@Service
public class ItemRequestMapper {
    ItemRequest itemRequestDtoToItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(itemRequestDto.getCreated());
        itemRequest.setDescription(itemRequestDto.getDescription());
        if (itemRequestDto.getRequester() != null) {
            itemRequest.setRequester(itemRequestDto.getRequester());
        }
        return itemRequest;
    }

    ItemRequestDto itemRequestToItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequester(itemRequest.getRequester());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }
}
