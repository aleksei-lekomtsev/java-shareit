package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadInputDataException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static ru.practicum.shareit.booking.Status.APPROVED;
import static ru.practicum.shareit.booking.Status.REJECTED;
import static ru.practicum.shareit.util.Util.checkForNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl {
    private final BookingRepository bookingRepository;
    private final UserRepository    userRepository;
    private final ItemRepository    itemRepository;

    public Booking create(Long userId, BookingDto entity) {
        checkForNull(entity);

        if (entity.getStart().isEqual(entity.getEnd()) || entity.getStart().isAfter(entity.getEnd())) {
            throw new BadInputDataException("Произошла непредвиденная ошибка.");
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", userId);
                    throw new EntityNotFoundException(User.class,
                            "Пользователь с id=" + userId + " не существует.");
                });
        Item item = itemRepository.findByIdAndOwnerIdNot(entity.getItemId(), userId);

        if (item == null) {
            throw new EntityNotFoundException(Item.class, "Entity with id=" + entity.getItemId() + " не существует.");
        }

        if (!item.getAvailable()) {
            throw new BadInputDataException("Произошла непредвиденная ошибка.");
        }

        return bookingRepository.save(BookingMapper.toBooking(entity, item, user, Status.WAITING));
    }

    public Booking update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findByItemOwnerIdAndId(userId, bookingId);

        if (booking == null) {
            throw new EntityNotFoundException(Booking.class, "Entity with id=" + bookingId + " doesn't exist.");
        }

        if (booking.getStatus().equals(APPROVED)) {
            throw new BadInputDataException("Status of booking is already approved");
        }

        booking.setStatus(approved ? APPROVED : REJECTED);
        return bookingRepository.save(booking);
    }

    public Booking findById(Long userId, Long id) {
        userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", userId);
                    throw new EntityNotFoundException(User.class, "Пользователь с id=" + userId + " не существует.");
                });
        Booking result = bookingRepository.findByIdForOwnerOrBooker(userId, id);
        if (result == null) {
            throw new EntityNotFoundException(Booking.class, "Entity with id=" + id + " doesn't exist.");
        }
        return result;
    }

    public Collection<Booking> findAll(Long bookerId, String state) {
        User user = userRepository.findById(bookerId).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", bookerId);
                    throw new EntityNotFoundException(User.class, "Пользователь с id=" + bookerId + " не существует.");
                });

        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findByBookerOrderByStartDesc(user);
            case FUTURE:
                return bookingRepository.findByBookerAndStartAfterOrderByStartDesc(user, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByBookerAndStatusOrderByStartDesc(user, Status.WAITING);
            case REJECTED:
                return bookingRepository.findByBookerAndStatusOrderByStartDesc(user, REJECTED);
            case CURRENT:
                return bookingRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user,
                        LocalDateTime.now(), LocalDateTime.now());
            case PAST:
                return bookingRepository.findByBookerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now());
            default:
                throw new RuntimeException("Unexpected state was received");
        }
    }

    public Collection<Booking> findAllForOwner(Long ownerId, String state) {
        User user = userRepository.findById(ownerId).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", ownerId);
                    throw new EntityNotFoundException(User.class, "Пользователь с id=" + ownerId + " не существует.");
                });
        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findByItemOwnerOrderByStartDesc(user);
            case FUTURE:
                return bookingRepository.findByItemOwnerAndStartAfterOrderByStartDesc(user, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByItemOwnerAndStatusOrderByStartDesc(user, Status.WAITING);
            case REJECTED:
                return bookingRepository.findByItemOwnerAndStatusOrderByStartDesc(user, REJECTED);
            case CURRENT:
                return bookingRepository.findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(user,
                        LocalDateTime.now(), LocalDateTime.now());
            case PAST:
                return bookingRepository.findByItemOwnerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now());

            default:
                throw new RuntimeException("Unexpected state was received");
        }
    }
}
