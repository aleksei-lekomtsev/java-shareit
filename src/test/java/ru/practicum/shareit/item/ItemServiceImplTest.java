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
    }

    @Test
    void findById() {
        Long          id               = 0L;
        Long          userId           = 0L;
        List<Comment> expectedComments = List.of();
        User          user             = new User();
        user.setId(userId);
        Item expectedItem = new Item();
        expectedItem.setOwner(user);
        Optional<Item> expected = Optional.of(expectedItem);
        when(itemRepository.findById(id)).thenReturn(expected);
        when(commentRepository.findByItemId(id)).thenReturn(expectedComments);
        when(bookingRepository.findFirst1ByItemIdAndStartBeforeAndStatusOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), eq(Status.APPROVED))).thenReturn(null);
        when(bookingRepository.findFirst1ByItemIdAndStartAfterAndStatusOrderByStartAsc(anyLong(),
                any(LocalDateTime.class), eq(Status.APPROVED))).thenReturn(null);
        ItemDto actual = service.findById(userId, id);

        assertEquals(itemMapper.toItemDto(expectedItem, null, null,
                expectedComments.stream().map(commentMapper::toDto).collect(Collectors.toList())), actual);
    }

    @Test
    void findByIdNotFoundItem() {
        Long id     = 0L;
        Long userId = 0L;
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findById(userId, id));
    }

    @Test
    void create() {
        Long           userId    = 0L;
        Long           itemId    = 0L;
        Long           requestId = 0L;
        Optional<User> user      = Optional.of(new User());
        ItemDto        itemDto   = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setRequestId(requestId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        Item item = new Item();
        item.setId(itemId);
        item.setItemRequest(itemRequest);
        item.setOwner(user.get());
        when(userRepository.findById(userId)).thenReturn(user);
        when(itemRequestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto actual = service.create(userId, itemDto);

        assertEquals(itemDto, actual);
        verify(itemRepository).save(item);
    }

    @Test
    void createNotFoundUser() {
        Long    userId    = 0L;
        Long    itemId    = 0L;
        Long    requestId = 0L;
        ItemDto itemDto   = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setRequestId(requestId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.create(userId, itemDto));
    }

    @Test
    void createNotFoundItem() {
        Long           userId    = 0L;
        Long           itemId    = 0L;
        Long           requestId = 0L;
        Optional<User> user      = Optional.of(new User());
        ItemDto        itemDto   = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setRequestId(requestId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        Item item = new Item();
        item.setId(itemId);
        item.setItemRequest(itemRequest);
        item.setOwner(user.get());
        when(userRepository.findById(userId)).thenReturn(user);
        when(itemRequestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.create(userId, itemDto));
    }

    @Test
    void createCommentNotFoundBooking() {
        Long userId = 0L;
        Long itemId = 0L;
        when(bookingRepository.findFirst1ByItemIdAndStatusAndStartBeforeAndBookerId(eq(itemId), eq(Status.APPROVED),
                any(LocalDateTime.class), eq(userId))).thenReturn(null);

        assertThrows(BadInputDataException.class, () -> service.create(userId, itemId, new CommentDto()));
    }

    @Test
    void update() {
        Long userId = 0L;
        Long id     = 0L;
        User user   = new User();
        user.setId(userId);
        Optional<User> userOptional = Optional.of(user);
        Item           item         = new Item();
        item.setId(id);
        item.setOwner(user);
        ItemDto expected = new ItemDto();
        expected.setId(id);
        expected.setName("name");
        expected.setDescription("description");
        expected.setAvailable(true);
        expected.setOwnerId(userId);
        Optional<Item> itemOptional = Optional.of(item);
        when(itemRepository.findById(id)).thenReturn(itemOptional);
        when(userRepository.findById(userId)).thenReturn(userOptional);

        ItemDto actual = service.update(userId, expected);

        assertEquals(expected, actual);
    }

    @Test
    void updateNotFoundItem() {
        Long    userId   = 0L;
        Long    id       = 0L;
        ItemDto expected = new ItemDto();
        expected.setId(id);
        expected.setName("name");
        expected.setDescription("description");
        expected.setAvailable(true);
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.update(userId, expected));
    }

    @Test
    void updateNotFoundUser() {
        Long           userId    = 0L;
        Long           itemId    = 0L;
        Long           requestId = 0L;
        Optional<User> user      = Optional.of(new User());
        ItemDto        itemDto   = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setRequestId(requestId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        Item item = new Item();
        item.setId(itemId);
        item.setItemRequest(itemRequest);
        item.setOwner(user.get());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.update(userId, itemDto));
    }

    @Test
    void findAll() {
        Long userId = 0L;
        Long id     = 0L;
        User user   = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(id);
        item.setOwner(user);
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
        Long userId = 0L;
        Long id     = 0L;
        User user   = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(id);
        item.setOwner(user);
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
        Long userId    = 0L;
        Long itemId    = 0L;
        Long bookingId = 0L;
        Long commentId = 0L;
        User user      = new User();
        user.setName("name");
        Optional<User> userOptional = Optional.of(user);
        Item           item         = new Item();
        item.setId(itemId);
        item.setOwner(userOptional.get());
        Optional<Item> itemOptional = Optional.of(item);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setItem(item);
        comment.setCreated(Instant.now().toEpochMilli());
        CommentDto commentDto = commentMapper.toDto(comment);

        when(bookingRepository.findFirst1ByItemIdAndStatusAndStartBeforeAndBookerId(eq(itemId), eq(Status.APPROVED),
                any(LocalDateTime.class), eq(userId))).thenReturn(booking);
        when(userRepository.findById(userId)).thenReturn(userOptional);
        when(itemRepository.findById(itemId)).thenReturn(itemOptional);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto actual = service.create(userId, itemId, commentMapper.toDto(comment));

        assertEquals(commentDto, actual);
    }

    @Test
    void createCommentNotFoundUser() {
        Long           userId       = 0L;
        Long           itemId       = 0L;
        Long           bookingId    = 0L;
        Long           commentId    = 0L;
        User           user         = new User();
        Optional<User> userOptional = Optional.of(user);
        Item           item         = new Item();
        item.setId(itemId);
        item.setOwner(userOptional.get());
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setItem(item);
        comment.setCreated(Instant.now().toEpochMilli());
        when(bookingRepository.findFirst1ByItemIdAndStatusAndStartBeforeAndBookerId(eq(itemId), eq(Status.APPROVED),
                any(LocalDateTime.class), eq(userId))).thenReturn(booking);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.create(userId, itemId,
                commentMapper.toDto(comment)));
    }

    @Test
    void createCommentNotFoundItem() {
        Long           userId       = 0L;
        Long           itemId       = 0L;
        Long           bookingId    = 0L;
        Long           commentId    = 0L;
        User           user         = new User();
        Optional<User> userOptional = Optional.of(user);
        Item           item         = new Item();
        item.setId(itemId);
        item.setOwner(userOptional.get());

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setItem(item);
        comment.setCreated(Instant.now().toEpochMilli());

        when(bookingRepository.findFirst1ByItemIdAndStatusAndStartBeforeAndBookerId(eq(itemId), eq(Status.APPROVED),
                any(LocalDateTime.class), eq(userId))).thenReturn(booking);
        when(userRepository.findById(userId)).thenReturn(userOptional);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.create(userId, itemId,
                commentMapper.toDto(comment)));
    }
}
