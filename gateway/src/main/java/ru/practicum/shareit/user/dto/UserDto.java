package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = Create.class, message = "Name cannot be empty or null")
    private String name;
    @Email
    @NotNull(groups = Create.class, message = "Email cannot be empty or null")
    private String email;
}
