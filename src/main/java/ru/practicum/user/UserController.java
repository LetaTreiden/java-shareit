package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.exceptions.ValidationException;

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
    public UserDTO findUserById(@PathVariable String userId) throws HttpClientErrorException.NotFound, ClassNotFoundException {
        return userService.findById(Long.valueOf(userId));
    }

    @GetMapping
    public Collection<UserDTO> findAllUsers() {
        return userService.findAll();
    }

    @PatchMapping("/{userId}")
    public UserDTO update(@PathVariable String userId, @Valid @RequestBody UserDTO userDto)
            throws ValidationException, ClassNotFoundException {
        return userService.update(Long.valueOf(userId), userDto);
    }

    @DeleteMapping("/{userId}")
    public Long deleteUser(@PathVariable String userId) throws HttpClientErrorException.NotFound, ClassNotFoundException {
        return userService.delete(Long.valueOf(userId));
    }

}