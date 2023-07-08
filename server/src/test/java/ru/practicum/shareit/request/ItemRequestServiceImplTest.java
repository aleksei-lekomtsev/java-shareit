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

    private Long           userId;
    private User           user;
    private Optional<User> userOptional;
    private Long           requestId;
    private ItemRequest    itemRequest;
    private LocalDateTime  created;
    private Long           itemId;
    private Item           item;
    private Optional<Item> itemOptional;

    @BeforeEach
    void beforeEach() {
        ReflectionTestUtils.setField(
                itemRequestService,
                "itemRequestMapper",
                itemRequestMapper
        );

        userId = 0L;
        user = new User();
        user.setId(userId);
        user.setName("name");
        userOptional = Optional.of(user);
        requestId = 0L;
        itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setRequestor(user);
        created = LocalDateTime.now();
        itemRequest.setCreated(created);
        item = new Item();
        item.setId(itemId);
        item.setOwner(user);
        item.setItemRequest(itemRequest);
        itemOptional = Optional.of(item);
    }

    @Test
    void findById() {
        Optional<ItemRequest> expected = Optional.of(itemRequest);
        when(userRepository.findById(userId)).thenReturn(userOptional);
        when(itemRepository.findByItemRequestId(itemRequest.getId())).thenReturn(new ArrayList<>());
        when(itemRequestRepository.findById(requestId)).thenReturn(expected);

        ItemRequestDto actual = itemRequestService.findById(userId, requestId);

        assertEquals(itemRequestMapper.toDto(expected.get()), actual);
    }

    @Test
    void findByIdNotFoundUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.findById(userId, requestId));
    }

    @Test
    void findByIdNotFoundItemRequest() {
        when(userRepository.findById(userId)).thenReturn(userOptional);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.findById(userId, requestId));
    }

    @Test
    void create() {
        when(userRepository.findById(userId)).thenReturn(userOptional);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);

        ItemRequestDto actual = itemRequestService.create(userId, itemRequestMapper.toDto(itemRequest), created);

        assertEquals(itemRequestMapper.toDto(itemRequest), actual);
        verify(itemRequestRepository).save(itemRequest);
    }

    @Test
    void createNotFoundUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.create(userId,
                itemRequestMapper.toDto(itemRequest), created));
    }

    @Test
    void findAll() {
        List<ItemRequest> itemsRequests = List.of(itemRequest);
        List<Item>        items         = List.of(item);
        when(userRepository.findById(userId)).thenReturn(userOptional);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)).thenReturn(itemsRequests);
        when(itemRepository.findByItemRequestId(requestId)).thenReturn(items);

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
                        requestId))
                .collect(Collectors.toList()));
        assertEquals(expected, all);
    }

    @Test
    void findAllNotFoundUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.findAll(userId));
    }

    @Test
    void findAllWithPage() {
        List<ItemRequest> itemsRequests = List.of(itemRequest);
        List<Item>        items         = List.of(item);
        PageRequest       page         = new ItemRequestPageRequest(0, 10);
        Page<ItemRequest> expectedPage = new PageImpl<>(itemsRequests);

        when(itemRequestRepository.findAllByRequestorIdNot(userId, page)).thenReturn(expectedPage);
        when(itemRepository.findByItemRequestId(requestId)).thenReturn(items);

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
                        requestId))
                .collect(Collectors.toList()));
        assertEquals(expected, all);
    }
}
