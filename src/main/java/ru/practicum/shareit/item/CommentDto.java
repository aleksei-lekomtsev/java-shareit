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

    // Привет, Семен!
    // > А почему здесь не прописал аннотацию валидации?
    // CommentDto используется, например, в createComment методе ItemController класса
    // Если я правильно понял, то при создании комментария в body POST request поля authorName, created могут
    // отсутствовать. Поэтому я не стал добавлять аннотацию @NotBlank
    private Long created;
}
