package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.BadInputDataException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl service;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    private Long           userId;
    private User           user;
    private Optional<User> userOptional;
    private Long           requestId;
    private ItemRequest    itemRequest;
    private Long           itemId;
    private Item           item;
    private Optional<Item> itemOptional;
    private Long           bookingId;
    private Booking        booking;
    private Long           commentId;
    private Comment        comment;

    @BeforeEach
    void beforeEach() {
        ReflectionTestUtils.setField(
                service,
                "commentMapper",
                commentMapper
        );

        ReflectionTestUtils.setField(
                service,
                "itemMapper",
                itemMapper
        );

        userId = 0L;
        user = new User();
        user.setId(userId);
        user.setName("name");
        userOptional = Optional.of(user);
        requestId = 0L;
        itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemId = 0L;
        item = new Item();
        item.setId(itemId);
        item.setOwner(user);
        item.setItemRequest(itemRequest);
        itemOptional = Optional.of(item);
        bookingId = 0L;
        booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        commentId = 0L;
        comment = new Comment();
        comment.setId(commentId);
        comment.setItem(item);
        comment.setCreated(Instant.now().toEpochMilli());
    }

    @Test
    void findById() {
        List<Comment> expected = List.of();
        when(itemRepository.findById(itemId)).thenReturn(itemOptional);
        when(commentRepository.findByItemId(itemId)).thenReturn(expected);
        when(bookingRepository.findFirst1ByItemIdAndStartBeforeAndStatusOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), eq(Status.APPROVED))).thenReturn(null);
        when(bookingRepository.findFirst1ByItemIdAndStartAfterAndStatusOrderByStartAsc(anyLong(),
                any(LocalDateTime.class), eq(Status.APPROVED))).thenReturn(null);
        ItemDto actual = service.findById(userId, itemId);

        assertEquals(itemMapper.toItemDto(item, null, null,
                expected.stream().map(commentMapper::toDto).collect(Collectors.toList())), actual);
    }

    @Test
    void findByIdNotFoundItem() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findById(userId, itemId));
    }

    @Test
    void create() {
        ItemDto expected = new ItemDto();
        expected.setId(itemId);
        expected.setRequestId(requestId);
        expected.setOwnerId(userId);
        when(userRepository.findById(userId)).thenReturn(userOptional);
        when(itemRequestRepository.findById(expected.getRequestId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto actual = service.create(userId, expected);

        assertEquals(expected, actual);
        verify(itemRepository).save(item);
    }

    @Test
    void createNotFoundUser() {
        ItemDto expected = new ItemDto();
        expected.setId(itemId);
        expected.setRequestId(requestId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.create(userId, expected));
    }

    @Test
    void createNotFoundItem() {
        ItemDto expected = new ItemDto();
        expected.setId(itemId);
        expected.setRequestId(requestId);
        when(userRepository.findById(userId)).thenReturn(userOptional);
        when(itemRequestRepository.findById(expected.getRequestId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.create(userId, expected));
    }

    @Test
    void createCommentNotFoundBooking() {
        when(bookingRepository.findFirst1ByItemIdAndStatusAndStartBeforeAndBookerId(eq(itemId), eq(Status.APPROVED),
                any(LocalDateTime.class), eq(userId))).thenReturn(null);

        assertThrows(BadInputDataException.class, () -> service.create(userId, itemId, new CommentDto()));
    }

    @Test
    void update() {
        ItemDto expected = new ItemDto();
        expected.setId(itemId);
        expected.setName("name");
        expected.setDescription("description");
        expected.setAvailable(true);
        expected.setOwnerId(userId);
        expected.setRequestId(requestId);
        when(itemRepository.findById(itemId)).thenReturn(itemOptional);
        when(userRepository.findById(userId)).thenReturn(userOptional);

        ItemDto actual = service.update(userId, expected);

        assertEquals(expected, actual);
    }

    @Test
    void updateNotFoundItem() {
        ItemDto expected = new ItemDto();
        expected.setId(itemId);
        expected.setName("name");
        expected.setDescription("description");
        expected.setAvailable(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.update(userId, expected));
    }

    @Test
    void updateNotFoundUser() {
        ItemDto expected = new ItemDto();
        expected.setId(itemId);
        expected.setRequestId(requestId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.update(userId, expected));
    }

    @Test
    void findAll() {
        List<Item> expected = List.of(item);
        when(itemRepository.findByOwnerId(userId)).thenReturn(expected);
        when(bookingRepository.findFirst1ByItemIdAndStartBeforeAndStatusOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), eq(Status.APPROVED))).thenReturn(null);
        when(bookingRepository.findFirst1ByItemIdAndStartAfterAndStatusOrderByStartAsc(anyLong(),
                any(LocalDateTime.class), eq(Status.APPROVED))).thenReturn(null);

        Collection<ItemDto> actual = service.findAll(userId);

        assertEquals(expected
                .stream()
                .map(i -> itemMapper.toItemDto(i, null, null, null))
                .collect(Collectors.toList()), actual);
    }

    @Test
    void search() {
        String text = "text";
        item.setName(text);
        List<Item> expected = List.of(item);
        when(itemRepository.search(text)).thenReturn(expected);

        Collection<ItemDto> actual = service.search(text);

        assertEquals(expected
                .stream()
                .map(i -> itemMapper.toItemDto(i, null, null, null))
                .collect(Collectors.toList()), actual);
    }

    @Test
    void searchWithEmptyText() {
        String     text     = "";
        List<Item> expected = List.of();

        Collection<ItemDto> actual = service.search(text);

        assertEquals(expected
                .stream()
                .map(i -> itemMapper.toItemDto(i, null, null, null))
                .collect(Collectors.toList()), actual);
        verify(itemRepository, never()).search(text);
    }

    @Test
    void createComment() {
        CommentDto expected = commentMapper.toDto(comment);
        when(bookingRepository.findFirst1ByItemIdAndStatusAndStartBeforeAndBookerId(eq(itemId), eq(Status.APPROVED),
                any(LocalDateTime.class), eq(userId))).thenReturn(booking);
        when(userRepository.findById(userId)).thenReturn(userOptional);
        when(itemRepository.findById(itemId)).thenReturn(itemOptional);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto actual = service.create(userId, itemId, commentMapper.toDto(comment));

        assertEquals(expected, actual);
    }

    @Test
    void createCommentNotFoundUser() {
        when(bookingRepository.findFirst1ByItemIdAndStatusAndStartBeforeAndBookerId(eq(itemId), eq(Status.APPROVED),
                any(LocalDateTime.class), eq(userId))).thenReturn(booking);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.create(userId, itemId,
                commentMapper.toDto(comment)));
    }

    @Test
    void createCommentNotFoundItem() {
        when(bookingRepository.findFirst1ByItemIdAndStatusAndStartBeforeAndBookerId(eq(itemId), eq(Status.APPROVED),
                any(LocalDateTime.class), eq(userId))).thenReturn(booking);
        when(userRepository.findById(userId)).thenReturn(userOptional);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.create(userId, itemId,
                commentMapper.toDto(comment)));
    }
}
