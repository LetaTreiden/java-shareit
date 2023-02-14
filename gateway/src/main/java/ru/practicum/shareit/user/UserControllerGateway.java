package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoGateway;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserControllerGateway {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody UserDtoGateway user) {
        log.info("Creating user {}", user);
        return userClient.addUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Valid @PathVariable Long userId, @RequestBody UserDtoGateway user) {
        log.info("Updating user {}, userId={}", user, userId);
        return userClient.updateUser(userId, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("Delete user {}", id);
        return userClient.deleteUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Get all users");
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        log.info("Get user {}", id);
        return userClient.getUser(id);
    }

}
