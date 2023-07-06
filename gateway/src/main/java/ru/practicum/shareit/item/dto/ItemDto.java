package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;
import ru.practicum.shareit.item.ItemBasicInfo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Name must not be null and must contain at least one non-whitespace character.",
            groups = ItemBasicInfo.class)
    private String name;

    @NotBlank(message = "Description  must not be null and must contain at least one non-whitespace character.",
            groups = ItemBasicInfo.class)
    private String description;

    @NotNull(groups = ItemBasicInfo.class)
    private Boolean available;

    private Long ownerId;

    private LastNextBookingDto lastBooking;

    private LastNextBookingDto nextBooking;

    private List<CommentDto> comments;

    private Long requestId;
}
