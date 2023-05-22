package ru.practicum.shareit.item.dto;

import lombok.Data;


@Data
public class ItemUpdateDto {
    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private Integer ownerId;

    public ItemUpdateDto(Integer id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
