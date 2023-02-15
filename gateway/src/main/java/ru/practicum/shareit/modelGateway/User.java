package ru.practicum.shareit.modelGateway;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class User {

    private long id;

    @Size(min = 1, max = 100)
    private String name;

    @Email
    @NotBlank
    private String email;

}
