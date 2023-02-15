package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDTO create(@RequestBody UserDTO user) {
        log.info("Добавление нового пользователя");
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDTO update(@PathVariable Long userId, @RequestBody UserDTO user) {
        log.info("Обновление данных о пользователе с id {}", userId);
        return userService.update(userId, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Удаление пользователя с id {}", id);
        userService.delete(id);
    }

    @GetMapping
    public Collection<UserDTO> getAll() {
        log.info("Получение списка всех пользователей");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDTO get(@PathVariable Long id) {
        log.info("Получение пользователя с id {}", id);
        return userService.get(id);
    }
}
