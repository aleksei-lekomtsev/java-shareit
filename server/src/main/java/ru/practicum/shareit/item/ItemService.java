package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> findAll(Long userId);

    ItemDto create(Long userId, ItemDto entity);

    ItemDto update(Long userId, ItemDto entity);

    ItemDto findById(Long userId, Long id);

    void delete(Long id);

    Collection<ItemDto> search(String text);

    CommentDto create(Long userId, Long itemId, CommentDto entity);
}
