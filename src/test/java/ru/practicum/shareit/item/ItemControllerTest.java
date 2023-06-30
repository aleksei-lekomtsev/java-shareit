package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Test
    void getItemsRequestWhenEmpty() {
        Collection<ItemDto> all = itemService.findAll(0L);

        assertTrue(all.isEmpty());
    }

    @Test
    void getUsers() {
        List<ItemDto> expected = List.of(new ItemDto(0L, "name", "description",
                true, 0L, null, null, new ArrayList<>(), 0L));
        Mockito
                .when(itemService.findAll(0L))
                .thenReturn(expected);

        Collection<ItemDto> all = itemController.getItems(0L);

        assertEquals(expected, all);
    }
}