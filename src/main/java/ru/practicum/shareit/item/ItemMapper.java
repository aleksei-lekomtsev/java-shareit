package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

public class ItemMapper {

    public static ItemCreateDto toItemDto(Item item) {
        return new ItemCreateDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static Item toItem(ItemCreateDto item) {
        return new Item(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId()
        );
    }

    public static Item toItem(ItemUpdateDto item) {
        Item result =  new Item();
        if (item.getId() != null) {
            result.setId(item.getId());
        }
        if (item.getName() != null) {
            result.setName(item.getName());
        }
        if (item.getDescription() != null) {
            result.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            result.setAvailable(item.getAvailable());
        }
        if (item.getOwnerId() != null) {
            result.setOwnerId(item.getOwnerId());
        }
        return result;
    }
}
