package ru.practicum.shareit.item;

import lombok.Data;

@Data
public class Item {
    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private Integer ownerId;

    public Item() {
    }

    public Item(String name, String description, Boolean available, Integer ownerId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerId = ownerId;
    }
}
