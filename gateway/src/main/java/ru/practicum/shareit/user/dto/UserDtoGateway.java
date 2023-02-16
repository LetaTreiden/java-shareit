package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDtoGateway {

    private long id;

    @NotBlank
    private String name;

    @Email
    @NotEmpty
    private String email;
}
