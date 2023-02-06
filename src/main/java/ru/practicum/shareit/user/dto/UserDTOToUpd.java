package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDTOToUpd {

    private long id;

    @NotBlank(groups = {Create.class})
    private String name;

    @NotBlank
    private String email;
}
