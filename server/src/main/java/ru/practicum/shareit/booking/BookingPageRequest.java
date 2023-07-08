package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class BookingPageRequest extends PageRequest {
    public BookingPageRequest(int from, int size) {
        super(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, "start"));
    }
}
