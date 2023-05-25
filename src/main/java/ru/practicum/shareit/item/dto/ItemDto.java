package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.ItemBasicInfo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    private Integer id;

    @NotBlank(message = "Name must not be null and must contain at least one non-whitespace character.",
            groups = ItemBasicInfo.class)
    private String name;

    @NotBlank(message = "Description  must not be null and must contain at least one non-whitespace character.",
            groups = ItemBasicInfo.class)
    private String description;

    @NotNull(groups = ItemBasicInfo.class)
    private Boolean available;

    private Integer ownerId;
}
