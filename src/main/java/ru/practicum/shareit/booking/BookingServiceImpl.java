package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadInputDataException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.Status.APPROVED;
import static ru.practicum.shareit.booking.Status.REJECTED;
import static ru.practicum.shareit.util.Util.checkForNull;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository    userRepository;
    private final ItemRepository    itemRepository;

    @Transactional
    @Override
    public BookingDto create(Long userId, BookingDto entity) {
        checkForNull(entity);

        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", userId);
                    throw new EntityNotFoundException(User.class,
                            String.format("Entity with id=%d doesn't exist.", userId));
                });
        Item item = itemRepository.findByIdAndOwnerIdNot(entity.getItemId(), userId);

        if (item == null) {
            throw new EntityNotFoundException(Item.class,
                    String.format("Entity with id=%d doesn't exist.", entity.getItemId()));
        }

        if (!item.getAvailable()) {
            throw new BadInputDataException("Произошла непредвиденная ошибка.");
        }

        return BookingMapper.toDto(bookingRepository.save(BookingMapper.toBooking(entity, item, user, Status.WAITING)));
    }

    @Transactional
    @Override
    public BookingDto update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findByItemOwnerIdAndId(userId, bookingId);

        if (booking == null) {
            throw new EntityNotFoundException(Booking.class,
                    String.format("Entity with id=%d doesn't exist.", bookingId));
        }

        if (booking.getStatus().equals(APPROVED)) {
            throw new BadInputDataException("Status of booking is already approved");
        }

        booking.setStatus(approved ? APPROVED : REJECTED);
        return BookingMapper.toDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto findById(Long userId, Long id) {
        userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", userId);
                    throw new EntityNotFoundException(User.class,
                            String.format("Entity with id=%d doesn't exist.", userId));
                });
        Booking result = bookingRepository.findByIdForOwnerOrBooker(userId, id);
        if (result == null) {
            throw new EntityNotFoundException(Booking.class,
                    String.format("Entity with id=%d doesn't exist.", id));
        }
        return BookingMapper.toDto(result);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> findAll(Long bookerId, String state) {
        User user = userRepository.findById(bookerId).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", bookerId);
                    throw new EntityNotFoundException(User.class,
                            String.format("Entity with id=%d doesn't exist.", bookerId));
                });

        Collection<Booking> result = new ArrayList<>();

        switch (State.valueOf(state)) {
            case ALL:
                result = bookingRepository.findByBookerOrderByStartDesc(user);
                break;
            case FUTURE:
                result = bookingRepository.findByBookerAndStartAfterOrderByStartDesc(user, LocalDateTime.now());
                break;
            case WAITING:
                result = bookingRepository.findByBookerAndStatusOrderByStartDesc(user, Status.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findByBookerAndStatusOrderByStartDesc(user, REJECTED);
                break;
            case CURRENT:
                result = bookingRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                result = bookingRepository.findByBookerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now());
                break;
        }

        return result
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> findAllForOwner(Long ownerId, String state) {
        User user = userRepository.findById(ownerId).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", ownerId);
                    throw new EntityNotFoundException(User.class,
                            String.format("Entity with id=%d doesn't exist.", ownerId));
                });

        Collection<Booking> result = new ArrayList<>();

        switch (State.valueOf(state)) {
            case ALL:
                result = bookingRepository.findByItemOwnerOrderByStartDesc(user);
                break;
            case FUTURE:
                result = bookingRepository.findByItemOwnerAndStartAfterOrderByStartDesc(user, LocalDateTime.now());
                break;
            case WAITING:
                result = bookingRepository.findByItemOwnerAndStatusOrderByStartDesc(user, Status.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findByItemOwnerAndStatusOrderByStartDesc(user, REJECTED);
                break;
            case CURRENT:
                result = bookingRepository.findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(user,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                result = bookingRepository.findByItemOwnerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now());
                break;
        }

        return result
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
