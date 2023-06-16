package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;

    @NotBlank(message = "Name must not be null and must contain at least one non-whitespace character.",
            groups = CommentBasicInfo.class)

    private String text;

    private String authorName;

    private Long created;
}
