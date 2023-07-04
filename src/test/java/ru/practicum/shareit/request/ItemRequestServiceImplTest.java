package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);

    @BeforeEach
    void beforeEach() {
        ReflectionTestUtils.setField(
                itemRequestService,
                "itemRequestMapper",
                itemRequestMapper
        );
    }

    @Test
    void findById() {
        Long           userId      = 0L;
        Long           id          = 0L;
        Optional<User> user        = Optional.of(new User());
        ItemRequest    itemRequest = new ItemRequest();
        itemRequest.setId(id);
        List<Item> items = new ArrayList<>();

        Optional<ItemRequest> expected = Optional.of(itemRequest);
        when(userRepository.findById(userId)).thenReturn(user);
        when(itemRepository.findByItemRequestId(itemRequest.getId())).thenReturn(items);
        when(itemRequestRepository.findById(id)).thenReturn(expected);

        ItemRequestDto actual = itemRequestService.findById(userId, id);

        assertEquals(itemRequestMapper.toDto(expected.get()), actual);
    }

    @Test
    void findByIdNotFoundUser() {
        Long           userId      = 0L;
        Long           id          = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.findById(userId, id));
    }

    @Test
    void findByIdNotFoundItemRequest() {
        Long           userId      = 0L;
        Long           id          = 0L;
        Optional<User> user        = Optional.of(new User());
        ItemRequest    itemRequest = new ItemRequest();
        itemRequest.setId(id);

        when(userRepository.findById(userId)).thenReturn(user);
        when(itemRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.findById(userId, id));
    }

    @Test
    void create() {
        Long           userId      = 0L;
        Long           id          = 0L;
        Optional<User> user        = Optional.of(new User());
        ItemRequest    itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setRequestor(user.get());
        LocalDateTime created = LocalDateTime.now();
        itemRequest.setCreated(created);
        when(userRepository.findById(userId)).thenReturn(user);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);

        ItemRequestDto actual = itemRequestService.create(userId, itemRequestMapper.toDto(itemRequest), created);

        assertEquals(itemRequestMapper.toDto(itemRequest), actual);
        verify(itemRequestRepository).save(itemRequest);
    }

    @Test
    void createNotFoundUser() {
        Long           userId      = 0L;
        Long           id          = 0L;
        Optional<User> user        = Optional.of(new User());
        ItemRequest    itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setRequestor(user.get());
        LocalDateTime created = LocalDateTime.now();
        itemRequest.setCreated(created);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.create(userId,
                itemRequestMapper.toDto(itemRequest), created));
    }

    @Test
    void findAll() {
        Long userId        = 0L;
        Long itemRequestId = 0L;
        Long itemId        = 0L;
        User user          = new User();
        user.setId(userId);
        Optional<User> userOptional = Optional.of(user);
        ItemRequest    itemRequest  = new ItemRequest();
        itemRequest.setId(itemRequestId);
        List<ItemRequest> itemsRequests = List.of(itemRequest);

        Item       item  = new Item();
        List<Item> items = List.of(item);
        item.setId(itemId);

        when(userRepository.findById(userId)).thenReturn(userOptional);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)).thenReturn(itemsRequests);
        when(itemRepository.findByItemRequestId(itemId)).thenReturn(items);

        Collection<ItemRequestDto> all = itemRequestService.findAll(userId);
        List<ItemRequestDto> expected = itemsRequests
                .stream()
                .map(itemRequestMapper::toDto)
                .collect(Collectors.toList());
        expected.get(0).setItems(items
                .stream()
                .map(i -> new ItemDto(
                        i.getId(),
                        i.getName(),
                        i.getDescription(),
                        i.getAvailable(),
                        itemRequestId))
                .collect(Collectors.toList()));
        assertEquals(expected, all);
    }

    @Test
    void findAllNotFoundUser() {
        Long userId        = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.findAll(userId));
    }

    @Test
    void findAllWithPage() {
        Long userId        = 0L;
        Long itemRequestId = 0L;
        Long itemId        = 0L;
        User user          = new User();
        user.setId(userId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestId);
        List<ItemRequest> itemsRequests = List.of(itemRequest);
        Item              item          = new Item();
        List<Item>        items         = List.of(item);
        item.setId(itemId);
        PageRequest   page   = new ItemRequestPageRequest(0, 10);
        Page<ItemRequest> expectedPage = new PageImpl<>(itemsRequests);

        when(itemRequestRepository.findAllByRequestorIdNot(userId, page)).thenReturn(expectedPage);
        when(itemRepository.findByItemRequestId(itemId)).thenReturn(items);

        Collection<ItemRequestDto> all = itemRequestService.findAll(userId, 0, 10);
        List<ItemRequestDto> expected = itemsRequests
                .stream()
                .map(itemRequestMapper::toDto)
                .collect(Collectors.toList());
        expected.get(0).setItems(items
                .stream()
                .map(i -> new ItemDto(
                        i.getId(),
                        i.getName(),
                        i.getDescription(),
                        i.getAvailable(),
                        itemRequestId))
                .collect(Collectors.toList()));
        assertEquals(expected, all);
    }
}
