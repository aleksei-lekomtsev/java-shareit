package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemCreateDto> getItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService
                .findAll(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemCreateDto createItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @RequestBody @Valid ItemCreateDto itemCreateDto) {
        itemCreateDto.setOwnerId(userId);
        return ItemMapper.toItemDto(itemService.create(ItemMapper.toItem(itemCreateDto)));
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemCreateDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @RequestBody ItemUpdateDto itemDto, @PathVariable Integer itemId) {
        itemDto.setId(itemId);
        itemDto.setOwnerId(userId);
        return ItemMapper.toItemDto(itemService.update(ItemMapper.toItem(itemDto)));
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemCreateDto getItemById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
        return ItemMapper.toItemDto(itemService.findById(itemId));
    }


    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteItem(@PathVariable Integer itemId) {
        itemService.delete(itemId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemCreateDto> search(@RequestParam(name = "text", required = false) String text) {
        return itemService
                .search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
