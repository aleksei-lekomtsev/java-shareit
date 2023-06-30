package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.UserCreateBasicInfo;
import ru.practicum.shareit.user.UserUpdateBasicInfo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Name must not be null and must contain at least one non-whitespace character.",
            groups = UserCreateBasicInfo.class)
    private String name;

    @NotBlank(message = "Email must not be null and must contain at least one non-whitespace character.",
            groups = UserCreateBasicInfo.class)
    @Email(message = "Email must be well-formed.", groups = {UserCreateBasicInfo.class, UserUpdateBasicInfo.class})
    private String email;
}
