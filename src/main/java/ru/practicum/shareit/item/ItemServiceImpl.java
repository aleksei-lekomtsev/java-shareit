package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.BadInputDataException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Util.checkForNull;


@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository    itemRepository;
    private final UserRepository    userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public Collection<ItemDto> findAll(Long userId) {
        return itemRepository
                .findByOwnerId(userId)
                .stream()
                .map(i -> ItemMapper
                        .toItemDto(i,
                                bookingRepository.findFirst1ByItemIdAndStartBeforeAndStatusOrderByStartDesc(i.getId(),
                                        LocalDateTime.now(), Status.APPROVED),
                                bookingRepository.findFirst1ByItemIdAndStartAfterAndStatusOrderByStartAsc(i.getId(),
                                        LocalDateTime.now(), Status.APPROVED),
                                null)
                )
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    public ItemDto create(Long userId, ItemDto entity) {
        checkForNull(entity);
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", userId);
                    throw new EntityNotFoundException(User.class,
                            String.format("Entity with id=%d doesn't exist.", userId));
                });
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(entity, user)), null, null, null);
    }

    @Transactional
    public ItemDto update(Long userId, ItemDto entity) {
        checkForNull(entity);
        Item item = itemRepository.findById(entity.getId()).orElseThrow(
                () -> {
                    log.warn("Entity with id={} doesn't exist", entity.getId());
                    throw new EntityNotFoundException(Item.class,
                            String.format("Entity with id=%d doesn't exist.", entity.getId()));
                });
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", userId);
                    throw new EntityNotFoundException(User.class,
                            String.format("Entity with id=%d doesn't exist.", userId));
                });
        if (!Objects.equals(user.getId(), userId)) {
            log.error("Смена владельца не поддерживается.");
            throw new EntityNotFoundException(Item.class, "Смена владельца не поддерживается.");
        }

        if (entity.getName() != null) {
            item.setName(entity.getName());
        }
        if (entity.getDescription() != null) {
            item.setDescription(entity.getDescription());
        }
        if (entity.getAvailable() != null) {
            item.setAvailable(entity.getAvailable());
        }

        return ItemMapper.toItemDto(item, null, null, null);
    }

    public ItemDto findById(Long userId, Long id) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("Item with id={} not exist", id);
                    throw new EntityNotFoundException(Item.class,
                            String.format("Entity with id=%d doesn't exist.", id));
                });

        List<CommentDto> comments = commentRepository
                .findByItemId(id)
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        return userId.equals(item.getOwner().getId()) ?
                ItemMapper.toItemDto(item,
                        bookingRepository.findFirst1ByItemIdAndStartBeforeAndStatusOrderByStartDesc(id,
                                LocalDateTime.now(), Status.APPROVED),
                        bookingRepository.findFirst1ByItemIdAndStartAfterAndStatusOrderByStartAsc(id,
                                LocalDateTime.now(), Status.APPROVED),
                        comments)
                : ItemMapper.toItemDto(item, null, null, comments);

    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    public Collection<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository
                .search(text)
                .stream()
                .map(i -> ItemMapper.toItemDto(i, null, null, null))
                .collect(Collectors.toList());
    }

    public CommentDto create(Long userId, Long itemId, CommentDto entity) {
        checkForNull(entity);
        Booking byItemId = bookingRepository.findFirst1ByItemIdAndStatusAndStartBeforeAndBookerId(itemId,
                Status.APPROVED, LocalDateTime.now(), userId);
        if (byItemId == null) {
            throw new BadInputDataException(String.format("Booking wasn't found for item id: %d", itemId));
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", userId);
                    throw new EntityNotFoundException(User.class,
                            String.format("Entity with id=%d doesn't exist.", userId));
                });

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> {
                    log.warn("Item with id={} not exist", itemId);
                    throw new EntityNotFoundException(Item.class,
                            String.format("Entity with id=%d doesn't exist.", itemId));
                });

        return CommentMapper.toDto(commentRepository.save(CommentMapper.toComment(entity,
                item, user.getName(), Instant.now().toEpochMilli())));
    }
}
