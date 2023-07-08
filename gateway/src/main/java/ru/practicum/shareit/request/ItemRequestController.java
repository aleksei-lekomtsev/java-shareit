package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Util.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient client;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItemRequest(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                    @RequestBody @Validated(ItemRequestBasicInfo.class)
                                                    ItemRequestDto dto) {
        return client.create(userId, dto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemsRequests(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return client.findAll(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemsRequests(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                   @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero
                                                   int from,
                                                   @RequestParam(name = "size", defaultValue = "10") @Positive
                                                   int size) {
        return client.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                     @PathVariable Long requestId) {
        return client.findById(userId, requestId);
    }

}
