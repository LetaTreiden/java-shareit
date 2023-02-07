package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDTO {

    private long id;

    @NotBlank(groups = {Create.class})
    private String name;

    @Email
    @NotBlank
    private String email;
}
