package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;

import java.util.Collection;

public interface UserService {
    UserDTO create(UserDTO userDto);

    UserDTO update(Long userId, UserDTO userDto);

    void delete(Long id);

    Collection<UserDTO> getAll();

    UserDTO get(Long id);
}
