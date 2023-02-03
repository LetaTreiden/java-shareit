package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;

import java.util.Collection;

public interface UserService {
    UserDTO createUser(UserDTO userDto);

    UserDTO updateUser(Long userId, UserDTO userDto);

    void deleteUser(Long id);

    Collection<UserDTO> getAllUsers();

    UserDTO getUser(Long id);
}
