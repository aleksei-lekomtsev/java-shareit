package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserCreateDto {
    private Integer id;

    @NotNull
    private String name;

    @NotEmpty(message = "Email must not be empty.")
    @Email(message = "Email must be well-formed.")
    private String email;

    public UserCreateDto(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
