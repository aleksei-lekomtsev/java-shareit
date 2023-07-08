package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    private Long id;

    @NotBlank
    private String description;

    private User requestor;

    @NotNull
    private LocalDateTime created;
}
