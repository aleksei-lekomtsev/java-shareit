package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class ItemRequestPageRequest extends PageRequest {
    public ItemRequestPageRequest(int from, int size) {
        super(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, "created"));
    }
}
