package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserDtoGateway {

    private long id;

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @Email
    @NotBlank
    private String email;
}
