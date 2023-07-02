package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadInputDataException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.Status.REJECTED;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl service;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @BeforeEach
    void beforeEach() {
        ReflectionTestUtils.setField(
                service,
                "bookingMapper",
                bookingMapper
        );
    }

    @Test
    void findById() {
        Long           userId   = 0L;
        Long           id       = 0L;
        Optional<User> user     = Optional.of(new User());
        Booking        expected = new Booking();
        expected.setItem(new Item());
        expected.setStatus(Status.APPROVED);
        when(userRepository.findById(userId)).thenReturn(user);
        when(bookingRepository.findByIdForOwnerOrBooker(userId, id)).thenReturn(expected);

        BookingDto actual = service.findById(userId, id);

        assertEquals(bookingMapper.toDto(expected), actual);
    }

    @Test
    void findByIdNotFoundUser() {
        Long           userId   = 0L;
        Long           id       = 0L;
        Optional<User> user     = Optional.of(new User());
        Booking        expected = new Booking();
        expected.setItem(new Item());
        expected.setStatus(Status.APPROVED);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findById(userId, id));
    }

    @Test
    void findByIdNotFoundBooking() {
        Long           userId = 0L;
        Long           id     = 0L;
        Optional<User> user   = Optional.of(new User());
        when(userRepository.findById(userId)).thenReturn(user);
        when(bookingRepository.findByIdForOwnerOrBooker(userId, id)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> service.findById(userId, id));
    }

    @Test
    void create() {
        Long           userId = 0L;
        Long           id     = 0L;
        Long           itemId = 0L;
        Optional<User> user   = Optional.of(new User());
        Item           item   = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setBooker(user.get());
        booking.setStatus(Status.WAITING);

        when(userRepository.findById(userId)).thenReturn(user);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(itemRepository.findByIdAndOwnerIdNot(itemId, userId)).thenReturn(item);

        BookingDto actual = service.create(userId, bookingMapper.toDto(booking));

        assertEquals(bookingMapper.toDto(booking), actual);
        verify(bookingRepository).save(booking);
    }

    @Test
    void createNotFoundUser() {
        Long           userId = 0L;
        Long           id     = 0L;
        Long           itemId = 0L;
        Optional<User> user   = Optional.of(new User());
        Item           item   = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setBooker(user.get());
        booking.setStatus(Status.WAITING);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.create(userId, bookingMapper.toDto(booking)));
    }

    @Test
    void createNotFoundItem() {
        Long           userId = 0L;
        Long           id     = 0L;
        Long           itemId = 0L;
        Optional<User> user   = Optional.of(new User());
        Item           item   = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setBooker(user.get());
        booking.setStatus(Status.WAITING);
        when(userRepository.findById(userId)).thenReturn(user);
        when(itemRepository.findByIdAndOwnerIdNot(itemId, userId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> service.create(userId, bookingMapper.toDto(booking)));
    }

    @Test
    void createItemNotAvailable() {
        Long           userId = 0L;
        Long           id     = 0L;
        Long           itemId = 0L;
        Optional<User> user   = Optional.of(new User());
        Item           item   = new Item();
        item.setId(itemId);
        item.setAvailable(false);
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setBooker(user.get());
        booking.setStatus(Status.WAITING);
        when(userRepository.findById(userId)).thenReturn(user);
        when(itemRepository.findByIdAndOwnerIdNot(itemId, userId)).thenReturn(item);

        assertThrows(BadInputDataException.class, () -> service.create(userId, bookingMapper.toDto(booking)));
    }

    @Test
    void findAllOwner() {
        Long userId = 0L;
        User user   = new User();
        user.setId(userId);
        Optional<User> userOptional = Optional.of(user);
        Booking        booking      = new Booking();
        booking.setItem(new Item());
        booking.setStatus(Status.APPROVED);
        List<Booking> expected     = List.of(booking);
        Page<Booking> expectedPage = new PageImpl<>(expected);
        PageRequest   page   = new BookingPageRequest(0, 10);
        when(userRepository.findById(userId)).thenReturn(userOptional);
        when(bookingRepository.findByItemOwner(user, page)).thenReturn(expectedPage);
        when(bookingRepository.findByItemOwnerAndStartAfter(eq(user), any(LocalDateTime.class), eq(page)))
                .thenReturn(expectedPage);
        when(bookingRepository.findByItemOwnerAndStatus(user, Status.WAITING, page)).thenReturn(expectedPage);
        when(bookingRepository.findByItemOwnerAndStatus(user, Status.REJECTED, page)).thenReturn(expectedPage);
        when(bookingRepository.findByItemOwnerAndStartBeforeAndEndAfter(eq(user), any(LocalDateTime.class),
                any(LocalDateTime.class), eq(page))).thenReturn(expectedPage);
        when(bookingRepository.findByItemOwnerAndEndBefore(eq(user), any(LocalDateTime.class), eq(page)))
                .thenReturn(expectedPage);

        List<BookingDto> all      = service.findAll(userId, "ALL", 0, 10, true);
        List<BookingDto> future   = service.findAll(userId, "FUTURE", 0, 10, true);
        List<BookingDto> waiting  = service.findAll(userId, "WAITING", 0, 10, true);
        List<BookingDto> rejected = service.findAll(userId, "REJECTED", 0, 10, true);
        List<BookingDto> current  = service.findAll(userId, "CURRENT", 0, 10, true);
        List<BookingDto> past     = service.findAll(userId, "PAST", 0, 10, true);

        assertEquals(expected.stream().map(bookingMapper::toDto).collect(Collectors.toList()), all);
        assertEquals(expected.stream().map(bookingMapper::toDto).collect(Collectors.toList()), future);
        assertEquals(expected.stream().map(bookingMapper::toDto).collect(Collectors.toList()), waiting);
        assertEquals(expected.stream().map(bookingMapper::toDto).collect(Collectors.toList()), rejected);
        assertEquals(expected.stream().map(bookingMapper::toDto).collect(Collectors.toList()), current);
        assertEquals(expected.stream().map(bookingMapper::toDto).collect(Collectors.toList()), past);
    }

    @Test
    void findAllOwnerNotFoundUser() {
        Long userId = 0L;
        User user   = new User();
        user.setId(userId);
        Booking        booking      = new Booking();
        booking.setItem(new Item());
        booking.setStatus(Status.APPROVED);
        List<Booking> expected     = List.of(booking);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findAll(userId, "ALL", 0, 10, true));
    }

    @Test
    void findAllBooker() {
        Long userId = 0L;
        User user   = new User();
        user.setId(userId);
        Optional<User> userOptional = Optional.of(user);
        Booking        booking      = new Booking();
        booking.setItem(new Item());
        booking.setStatus(Status.APPROVED);
        List<Booking> expected     = List.of(booking);
        Page<Booking> expectedPage = new PageImpl<>(expected);
        PageRequest   page   = new BookingPageRequest(0, 10);
        when(userRepository.findById(userId)).thenReturn(userOptional);
        when(bookingRepository.findByBooker(user, page)).thenReturn(expectedPage);
        when(bookingRepository.findByBookerAndStartAfter(eq(user), any(LocalDateTime.class), eq(page)))
                .thenReturn(expectedPage);
        when(bookingRepository.findByBookerAndStatus(user, Status.WAITING, page)).thenReturn(expectedPage);
        when(bookingRepository.findByBookerAndStatus(user, Status.REJECTED, page)).thenReturn(expectedPage);
        when(bookingRepository.findByBookerAndStartBeforeAndEndAfter(eq(user), any(LocalDateTime.class),
                any(LocalDateTime.class), eq(page))).thenReturn(expectedPage);
        when(bookingRepository.findByBookerAndEndBefore(eq(user), any(LocalDateTime.class), eq(page)))
                .thenReturn(expectedPage);

        List<BookingDto> all      = service.findAll(userId, "ALL", 0, 10, false);
        List<BookingDto> future   = service.findAll(userId, "FUTURE", 0, 10, false);
        List<BookingDto> waiting  = service.findAll(userId, "WAITING", 0, 10, false);
        List<BookingDto> rejected = service.findAll(userId, "REJECTED", 0, 10, false);
        List<BookingDto> current  = service.findAll(userId, "CURRENT", 0, 10, false);
        List<BookingDto> past     = service.findAll(userId, "PAST", 0, 10, false);

        assertEquals(expected.stream().map(bookingMapper::toDto).collect(Collectors.toList()), all);
        assertEquals(expected.stream().map(bookingMapper::toDto).collect(Collectors.toList()), future);
        assertEquals(expected.stream().map(bookingMapper::toDto).collect(Collectors.toList()), waiting);
        assertEquals(expected.stream().map(bookingMapper::toDto).collect(Collectors.toList()), rejected);
        assertEquals(expected.stream().map(bookingMapper::toDto).collect(Collectors.toList()), current);
        assertEquals(expected.stream().map(bookingMapper::toDto).collect(Collectors.toList()), past);
    }

    @Test
    void update() {
        Long id     = 0L;
        Long userId = 0L;
        Long itemId = 0L;
        User user   = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setStatus(REJECTED);
        BookingDto expected = new BookingDto();
        expected.setId(id);
        expected.setStatus(Status.APPROVED.name());
        expected.setItem(item);
        expected.setItemId(itemId);
        when(bookingRepository.findByItemOwnerIdAndId(userId, itemId)).thenReturn(booking);

        BookingDto actual = service.update(userId, id, true);

        assertEquals(expected, actual);
    }

    @Test
    void updateNotFoundItemOwnerIdAndId() {
        Long id     = 0L;
        Long userId = 0L;
        Long itemId = 0L;
        User user   = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        BookingDto expected = new BookingDto();
        expected.setId(id);
        expected.setStatus(Status.APPROVED.name());
        when(bookingRepository.findByItemOwnerIdAndId(userId, itemId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> service.update(userId, id, true));
    }

    @Test
    void updateStatusAlreadyApproved() {
        Long id     = 0L;
        Long userId = 0L;
        Long itemId = 0L;
        User user   = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findByItemOwnerIdAndId(userId, itemId)).thenReturn(booking);

        assertThrows(BadInputDataException.class, () -> service.update(userId, id, true));
    }
}