package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;


@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequest toItemRequest(ItemRequestDto dto);

    @Mapping(target = "items", expression  = "java(new java.util.ArrayList())")
    ItemRequestDto toDto(ItemRequest itemRequest);
}
