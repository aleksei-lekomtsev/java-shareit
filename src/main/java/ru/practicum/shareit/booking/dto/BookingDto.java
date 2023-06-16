package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingBasicInfo;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long          id;
    private Long          itemId;

    @FutureOrPresent(groups = BookingBasicInfo.class)
    @NotNull(groups = BookingBasicInfo.class)
    private LocalDateTime start;

    @FutureOrPresent(groups = BookingBasicInfo.class)
    @NotNull(groups = BookingBasicInfo.class)
    private LocalDateTime end;
    private boolean       approved;
    private String        status;
    private User          booker;
    private Item          item;
}
