package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
public class ItemServiceImpl {
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
                            "Пользователь с id=" + userId + " не существует.");
                });
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(entity, user)), null, null, null);
    }

    public ItemDto update(Long userId, ItemDto entity) {
        checkForNull(entity);
        Item item = itemRepository.findById(entity.getId()).orElseThrow(
                () -> {
                    log.warn("Entity with id={} doesn't exist", entity.getId());
                    throw new EntityNotFoundException(Item.class, "Entity with id=" +
                            entity.getId() + " doesn't exist.");
                });
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", userId);
                    throw new EntityNotFoundException(User.class, "User with id=" + userId + " doesn't exist.");
                });
        if (!Objects.equals(user.getId(), userId)) {
            log.error("Смена владельца не поддерживается.");
            throw new EntityNotFoundException(Item.class, "Смена владельца не поддерживается.");
        }

        // > А проверки через if точно нужны?
        // Если я правильно все понимаю...
        // Когда происходит update в ItemDto entity может прийти новый description
        // а старый name не прийти, т.е. в базе должен обновиться description, а name остаться старый
        // Подобной проверкой я это учитываю и беру из Entity(item) старое значение name, чтобы
        // при вызове save метода ItemRepository класса поля объекта аргумента для save были заполнены
        if (entity.getName() == null) {
            entity.setName(item.getName());
        }
        if (entity.getDescription() == null) {
            entity.setDescription(item.getDescription());
        }
        if (entity.getAvailable() == null) {
            entity.setAvailable(item.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(entity, user)), null, null, null);
    }

    public ItemDto findById(Long userId, Long id) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("Item with id={} not exist", id);
                    throw new EntityNotFoundException(Item.class, "Вещь с id=" + id + " не существует.");
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
            throw new BadInputDataException("Booking wasn't found for item id: " + itemId);
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", userId);
                    throw new EntityNotFoundException(User.class,
                            "Пользователь с id=" + userId + " не существует.");
                });

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> {
                    log.warn("Item with id={} not exist", itemId);
                    throw new EntityNotFoundException(Item.class, "Вещь с id=" + itemId + " не существует.");
                });

        return CommentMapper.toDto(commentRepository.save(CommentMapper.toComment(entity,
                item, user.getName(), Instant.now().toEpochMilli())));
    }
}
