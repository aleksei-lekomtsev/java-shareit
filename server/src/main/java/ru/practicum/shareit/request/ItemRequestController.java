package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.util.Util.X_SHARER_USER_ID;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                            @RequestBody ItemRequestDto dto) {
        return service.create(userId, dto, LocalDateTime.now());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemRequestDto> getItemsRequests(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return service.findAll(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getItemsRequests(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @RequestParam(name = "size", defaultValue = "10") int size) {
        return service.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getItemRequestById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                             @PathVariable Long requestId) {
        return service.findById(userId, requestId);
    }
}
