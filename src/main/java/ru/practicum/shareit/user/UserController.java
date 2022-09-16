package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO Sprint add-controllers.
 */
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDTO create(@Valid @RequestBody UserDTO uDto) throws ValidationException {
        return userService.create(uDto);
    }

    @GetMapping("/{userId}")
    public UserDTO findUserById(@PathVariable String userId) throws NotFoundException {
        return userService.findById(Long.valueOf(userId));
    }

    @GetMapping
    public Collection<UserDTO> findAllUsers() {
        return userService.findAll();
    }

    @PatchMapping("/{userId}")
    public UserDTO update(@PathVariable String userId, @Valid @RequestBody UserDTO userDto) throws ValidationException,
            NotFoundException {
        return userService.update(Long.valueOf(userId), userDto);
    }

    @DeleteMapping("/{userId}")
    public Long deleteUser(@PathVariable String userId) throws NotFoundException {
        return userService.delete(Long.valueOf(userId));
    }

}