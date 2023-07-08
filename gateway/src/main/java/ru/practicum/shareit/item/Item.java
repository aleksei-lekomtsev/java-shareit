package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;
    private User owner;
    private ItemRequest itemRequest;
}
