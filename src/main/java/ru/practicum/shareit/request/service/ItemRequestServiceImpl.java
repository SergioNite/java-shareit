package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto addRequest(ItemRequestDto itemRequestDto, Long ownerId) {
        User requester = userRepository.findById(ownerId).orElseThrow(()
                -> new UserNotFoundException(String.format("User does not exist:%d", ownerId)));
        ItemRequest itemRequest = itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto);
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestMapper.itemRequestToItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findByOwnerId(Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(()
                -> new UserNotFoundException(String.format("User does not exist:%d", ownerId)));
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequesterIdOrderByCreatedAsc(ownerId);
        List<ItemRequestDto> itemDtoList = itemRequestList.stream()
                .map(itemRequestMapper::itemRequestToItemRequestDto)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findAllByRequestIn(itemRequestList);
        Map<Long, List<Item>> requestIdToItemList = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(), Collectors.toList()));

        itemDtoList.forEach(ird -> ird.setItems(getItemDtoList(ird.getId(), requestIdToItemList)));
        return itemDtoList;
    }

    @Override
    public List<ItemRequestDto> findAll(Long ownerId, Pageable pageable) {
        userRepository.findById(ownerId).orElseThrow(()
                -> new UserNotFoundException(String.format("User does not exist:%d", ownerId)));
        List<ItemRequest> itemRequestList = itemRequestRepository
                .findAllByRequesterIdNotOrderByCreatedDesc(ownerId, pageable);
        List<ItemRequestDto> itemDtoList = itemRequestList.stream()
                .map(itemRequestMapper::itemRequestToItemRequestDto)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findAllByRequestIn(itemRequestList);
        Map<Long, List<Item>> requestIdToItemList = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(), Collectors.toList()));

        itemDtoList.forEach(itemRequestDto -> itemRequestDto.setItems(getItemDtoList(itemRequestDto.getId(), requestIdToItemList)));
        return itemDtoList;
    }

    @Override
    public ItemRequestDto findById(Long ownerId, Long requestId) {
        userRepository.findById(ownerId).orElseThrow(()
                -> new UserNotFoundException(String.format("User does not exist:%d", ownerId)));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Request does not exist:%d", requestId)));
        ItemRequestDto itemRequestDto = itemRequestMapper.itemRequestToItemRequestDto(itemRequest);

        List<Item> itemList = itemRepository.findAllByRequest(itemRequest);
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (!itemList.isEmpty()) {
            itemDtoList = itemList.stream().map(itemMapper::toDtoItem).collect(Collectors.toList());
        }

        itemRequestDto.setItems(itemDtoList);
        return itemRequestDto;
    }


    private List<ItemDto> getItemDtoList(Long itemRequestId, Map<Long, List<Item>> requestIdToItemList) {
        List<ItemDto> itemDto = new ArrayList<>();
        if (requestIdToItemList.containsKey(itemRequestId)) {
            itemDto = requestIdToItemList.get(itemRequestId).stream()
                    .map(itemMapper::toDtoItem)
                    .collect(Collectors.toList());
        }
        return itemDto;
    }
}
