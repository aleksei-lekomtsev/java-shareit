package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

import static ru.practicum.shareit.util.Util.X_SHARER_USER_ID;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> getItems(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return service.findAll(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                              @RequestBody @Validated(ItemBasicInfo.class) ItemDto itemDto) {
        return service.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                              @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        itemDto.setId(itemId);
        return service.update(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItemById(@RequestHeader(X_SHARER_USER_ID) Long userId, @PathVariable Long itemId) {
        return service.findById(userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteItem(@PathVariable Long itemId) {
        service.delete(itemId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> search(@RequestParam(name = "text", required = false) String text) {
        return service.search(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto createComment(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody @Validated(CommentBasicInfo.class) CommentDto dto) {
        return service.create(userId, itemId, dto);
    }
}
