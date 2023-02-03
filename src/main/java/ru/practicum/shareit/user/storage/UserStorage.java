package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDTO;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    UserDTO addUser(UserDTO userDto);

    UserDTO updateUser(Long userId, UserDTO userDto);

    void deleteUser(Long id);

    Collection<UserDTO> getAllUsers();

    Optional<UserDTO> getUser(Long id);
}
