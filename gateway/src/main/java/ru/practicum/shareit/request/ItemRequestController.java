package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @Autowired
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                     @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Request form user {} was added", requestorId);
        return itemRequestClient.addRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsForRequestor(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        log.info("Get all requests for requestor {}", requestorId);
        return itemRequestClient.getAllRequestsForRequestor(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestParam(value = "from", required = false, defaultValue = "0")
                                               @Min(0) int from,
                                               @RequestParam(value = "size", required = false, defaultValue = "20")
                                               @Min(1) int size,
                                               @RequestHeader("X-Sharer-User-Id") long requestorId) {
        log.info("Get all requests");
        return itemRequestClient.getAllRequests(requestorId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getOneRequest(@PathVariable long requestId,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get request {}", requestId);
        return itemRequestClient.getOneRequest(requestId, userId);
    }
}
