package ru.practicum.shareit.user.service;


import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDTO;

import javax.validation.ValidationException;
import java.util.Collection;

public interface UserService {

    UserDTO patchUser(UserDTO userDto, Long userId);
}