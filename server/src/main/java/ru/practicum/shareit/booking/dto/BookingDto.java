package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long          id;
    private Long          itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean       approved;
    private String        status;
    private User          booker;
    private Item          item;
}
