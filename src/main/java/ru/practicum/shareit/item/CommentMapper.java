package ru.practicum.shareit.item;


public class CommentMapper {
    public static Comment toComment(CommentDto dto, Item item, String authorName, Long created) {
        return new Comment(
                dto.getId(),
                dto.getText(),
                item,
                authorName,
                created
        );
    }

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthorName(),
                comment.getCreated()
        );
    }
}
