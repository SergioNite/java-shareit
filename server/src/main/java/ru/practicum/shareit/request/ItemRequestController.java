package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto addRequest(@RequestBody ItemRequestDto itemRequestDto,
                                     @RequestHeader(USER_ID_HEADER) long ownerId) {
        return itemRequestService.addRequest(itemRequestDto, ownerId);
    }

    @GetMapping
    public List<ItemRequestDto> findByOwnerId(@RequestHeader(USER_ID_HEADER) long ownerId) {
        return itemRequestService.findByOwnerId(ownerId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader(USER_ID_HEADER) long ownerId,
                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.findAll(ownerId, PageRequest.of((from == 0 ? 0 : (from / size)), size));
    }

    @GetMapping("{requestId}")
    public ItemRequestDto findById(@RequestHeader(USER_ID_HEADER) long ownerId, @PathVariable long requestId) {
        return itemRequestService.findById(ownerId, requestId);
    }
}
