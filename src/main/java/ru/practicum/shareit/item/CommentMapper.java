package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "dto.id", target = "id")
    @Mapping(source = "authorName", target = "authorName")
    @Mapping(source = "created", target = "created")
    Comment toComment(CommentDto dto, Item item, String authorName, Long created);

    CommentDto toDto(Comment comment);
}
