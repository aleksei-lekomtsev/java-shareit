package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


public interface ItemRequestService {
    Collection<ItemRequestDto> findAll(Long userId);

    List<ItemRequestDto> findAll(Long userId, int from, int size);

    ItemRequestDto create(Long userId, ItemRequestDto entity, LocalDateTime created);

    ItemRequestDto findById(Long userId, Long id);
}
