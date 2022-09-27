package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;

public interface UserService {

    UserDTO patchUser(UserDTO userDto, Long userId);
}