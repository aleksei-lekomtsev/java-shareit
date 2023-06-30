package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Util.checkForNull;


@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository        userRepository;
    private final ItemRepository             itemRepository;
    private final ItemRequestMapper itemRequestMapper;

    private ItemRequestDto fetchItemsAndCreateItemRequestDto(ItemRequest itemRequest) {
        List<ItemDto> items = itemRepository
                .findByItemRequestId(itemRequest.getId())
                .stream()
                .map(i -> new ItemDto(
                        i.getId(),
                        i.getName(),
                        i.getDescription(),
                        i.getAvailable(),
                        itemRequest.getId()))
                .collect(Collectors.toList());

        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items
        );
    }

    @Transactional
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto dto, Long created) {
        checkForNull(dto);
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("Entity with id={} not exist", userId);
                    throw new EntityNotFoundException(User.class,
                            String.format("Entity with id=%d doesn't exist.", userId));
                });
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(dto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(created);
        return itemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public Collection<ItemRequestDto> findAll(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("Entity with id={} not exist", userId);
                    throw new EntityNotFoundException(User.class,
                            String.format("Entity with id=%d doesn't exist.", userId));
                });

        return itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map(this::fetchItemsAndCreateItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findAll(Long userId, int from, int size) {
        Sort        sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        PageRequest page          = PageRequest.of(from > 0 ? from / size : 0, size, sortByCreated);
        return itemRequestRepository
                .findAllByRequestorIdNot(userId, page)
                .map(this::fetchItemsAndCreateItemRequestDto)
                .getContent();
    }

    @Override
    public ItemRequestDto findById(Long userId, Long id) {
        userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("Entity with id={} not exist", userId);
                    throw new EntityNotFoundException(User.class,
                            String.format("Entity with id=%d doesn't exist.", userId));
                });

        return itemRequestRepository
                .findById(id)
                .map(this::fetchItemsAndCreateItemRequestDto)
                .orElseThrow(
                        () -> {
                            log.warn("Entity with id={} not exist", userId);
                            throw new EntityNotFoundException(ItemRequest.class,
                                    String.format("Entity with id=%d doesn't exist.", id));
                        });
    }
}
