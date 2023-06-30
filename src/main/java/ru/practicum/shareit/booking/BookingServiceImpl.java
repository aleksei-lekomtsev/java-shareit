package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.List;
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

        Long itemId = entity.getItemId();
        Item item = itemRepository.findByIdAndOwnerIdNot(itemId, userId);

        if (item == null) {
            throw new EntityNotFoundException(Item.class,
                    String.format("Entity with id=%d doesn't exist.", itemId));
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
    public List<BookingDto> findAll(Long bookerId, String state, int from, int size, Boolean owner) {
        User user = userRepository.findById(bookerId).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", bookerId);
                    throw new EntityNotFoundException(User.class,
                            String.format("Entity with id=%d doesn't exist.", bookerId));
                });

        Page<Booking> result      = null;
        Sort          sortByStart = Sort.by(Sort.Direction.DESC, "start");
        PageRequest   page        = PageRequest.of(from > 0 ? from / size : 0, size, sortByStart);

        switch (State.valueOf(state)) {
            case ALL:
                result = owner ?
                        bookingRepository.findByItemOwner(user, page) :
                        bookingRepository.findByBooker(user, page);
                break;
            case FUTURE:
                result = owner ?
                        bookingRepository.findByItemOwnerAndStartAfter(user, LocalDateTime.now(), page) :
                        bookingRepository.findByBookerAndStartAfter(user, LocalDateTime.now(), page);
                break;
            case WAITING:
                result = owner ?
                        bookingRepository.findByItemOwnerAndStatus(user, Status.WAITING, page) :
                        bookingRepository.findByBookerAndStatus(user, Status.WAITING, page);
                break;
            case REJECTED:
                result = owner ?
                        bookingRepository.findByItemOwnerAndStatus(user, REJECTED, page) :
                        bookingRepository.findByBookerAndStatus(user, REJECTED, page);
                break;
            case CURRENT:
                result = owner ?
                        bookingRepository.findByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                                LocalDateTime.now(), page) :
                        bookingRepository.findByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                                LocalDateTime.now(), page);
                break;
            case PAST:
                result = owner ?
                        bookingRepository.findByItemOwnerAndEndBefore(user, LocalDateTime.now(), page) :
                        bookingRepository.findByBookerAndEndBefore(user, LocalDateTime.now(), page);
                break;
        }

        return result
                .getContent()
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
