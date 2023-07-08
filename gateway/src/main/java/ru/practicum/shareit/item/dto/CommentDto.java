package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.CommentBasicInfo;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;

    @NotBlank(message = "Name must not be null and must contain at least one non-whitespace character.",
            groups = CommentBasicInfo.class)
    private String text;

    private String authorName;

    private Long created;
}
