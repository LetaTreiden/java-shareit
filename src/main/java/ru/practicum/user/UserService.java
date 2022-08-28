package ru.practicum.user;

import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;

import java.util.Collection;

public interface UserService {
    UserDTO create(UserDTO uDto) throws ValidationException;


    UserDTO findById(Long id) throws NotFoundException;

    Collection<UserDTO> findAll();

    UserDTO update(Long id, UserDTO uDto) throws ValidationException, NotFoundException;

    Long delete(Long userId) throws NotFoundException;


    void checkId(Long userId) throws NotFoundException;
}