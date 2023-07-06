package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequestBasicInfo;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Description must not be null and must contain at least one non-whitespace character.",
            groups = ItemRequestBasicInfo.class)
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}
