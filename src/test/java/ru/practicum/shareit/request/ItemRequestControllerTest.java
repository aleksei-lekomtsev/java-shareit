package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Test
    void getItemsRequestWhenEmpty() {
        Collection<ItemRequestDto> all = itemRequestService.findAll(0L);

        assertTrue(all.isEmpty());
    }

    @Test
    void getUsers() {
        List<ItemRequestDto> expected = List.of(new ItemRequestDto(0L, "expected", LocalDateTime.now(), null));
        Mockito
                .when(itemRequestService.findAll(0L))
                .thenReturn(expected);

        Collection<ItemRequestDto> all = itemRequestController.getItemsRequests(0L);

        assertEquals(expected, all);
    }
}