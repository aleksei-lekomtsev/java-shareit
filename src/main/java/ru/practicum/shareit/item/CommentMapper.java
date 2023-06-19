package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;


@UtilityClass
public class CommentMapper {
    public Comment toComment(CommentDto dto, Item item, String authorName, Long created) {
        return new Comment(
                dto.getId(),
                dto.getText(),
                item,
                authorName,
                created
        );
    }

    public CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthorName(),
                comment.getCreated()
        );
    }
}
