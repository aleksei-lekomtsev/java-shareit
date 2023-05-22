package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ItemCreateDto {
    private Integer id;

    @NotEmpty(message = "Name must not be empty.")
    private String name;

    @NotEmpty(message = "Description must not be empty.")
    private String description;

    @NotNull
    private Boolean available;

    private Integer ownerId;

    public ItemCreateDto(Integer id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
