package ru.practicum.user;

import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.exceptions.ValidationException;

import java.util.Collection;

public interface UserService {
    UserDTO create(UserDTO uDto) throws ValidationException;


    UserDTO findById(Long id) throws HttpClientErrorException.NotFound, ClassNotFoundException;

    Collection<UserDTO> findAll();

    UserDTO update(Long id, UserDTO uDto) throws ValidationException, ClassNotFoundException;

    Long delete(Long userId) throws HttpClientErrorException.NotFound, ClassNotFoundException;


    void checkId(Long userId) throws HttpClientErrorException.NotFound, ClassNotFoundException;
}