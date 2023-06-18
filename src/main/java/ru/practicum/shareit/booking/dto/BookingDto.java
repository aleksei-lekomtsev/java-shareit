package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingBasicInfo;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@StartBeforeEndDateValid(groups = BookingBasicInfo.class)
public class BookingDto {
    private Long          id;
    private Long          itemId;

    @Future(groups = BookingBasicInfo.class)
    @NotNull(groups = BookingBasicInfo.class)
    private LocalDateTime start;

    @Future(groups = BookingBasicInfo.class)
    @NotNull(groups = BookingBasicInfo.class)
    private LocalDateTime end;
    private boolean       approved;
    private String        status;
    private User          booker;
    private Item          item;
}
