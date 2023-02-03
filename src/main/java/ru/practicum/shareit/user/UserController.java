package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDTO createUser(@Valid @RequestBody UserDTO user) {
        log.info("Добавление нового пользователя");
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDTO update(@Valid @PathVariable Long userId, @RequestBody UserDTO user) {
        log.info("Обновление данных о пользователе с id {}", userId);
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Удаление пользователя с id {}", id);
        userService.deleteUser(id);
    }

    @GetMapping
    public Collection<UserDTO> getUsers() {
        log.info("Получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        log.info("Получение пользователя с id {}", id);
        return userService.getUser(id);
    }
}
