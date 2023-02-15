package ru.practicum.shareit.modelGateway;

import lombok.Data;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class User {

    private long id;

    @NotBlank(groups = {Create.class})
    private String name;

    @Email(groups = {Update.class, Create.class})
    @NotEmpty(groups = {Create.class})
    private String email;
}
