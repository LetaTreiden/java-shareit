package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDTO;

import java.util.*;

@Component
@Slf4j
public class UserInMemoryStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public UserDTO addUser(UserDTO userDto) {
        id = id + 1;
        userDto.setId(id);
        users.put(id, UserMapper.toUser(userDto));
        log.info("Добавлен пользователь: {},", userDto.getName());
        return userDto;
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO userDto) {
        userDto.setId(userId);
        User user = UserMapper.toUser(getUser(userId).get());
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        user.setId(userId);
        users.put(userId, user);
        log.info("Обновлены данные пользователя: {}", userDto.getName());
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
        log.info("Пользователь c id {} удален", id);
    }

    @Override
    public Collection<UserDTO> getAllUsers() {
        List<UserDTO> usersDto = new ArrayList<>();
        for (Long id : users.keySet()) {
            usersDto.add(UserMapper.toUserDto(users.get(id)));
        }
        log.info("Количество найденных пользвателей {}:", users.size());
        return usersDto;
    }

    @Override
    public Optional<UserDTO> getUser(Long id) {
        log.info("Пользователь с id {} найден", id);
        User user = users.get(id);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(UserMapper.toUserDto(user));
    }

}
