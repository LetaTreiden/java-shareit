package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.shareit.exceptions.InvalidParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UserRepositoryImpl implements UserRepository {

    private static long id = 0;

    private static Long generateUserId() {
        return ++id;
    }

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(generateUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public Map<Long, User> findAll() {
        return users;
    }

    @Override
    public User update(Long id, User user) {
        User userUpd = findById(id);

        if (user.getEmail() != null) {
            userUpd.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            userUpd.setName(user.getName());
        }

        return userUpd;
    }

    @Override
    public Long delete(Long id) {
        return users.remove(id).getId();
    }

    @Override
    public void checkId(Long id) throws HttpClientErrorException.NotFound, NotFoundException {
        if (!findAll().containsKey(id)) {
            throw new NotFoundException("Пользователь id %d не найден");
        }
    }

    @Override
    public void checkEmail(String email) throws ValidationException {
        if (email == null || email.isBlank()) {
            throw new InvalidParameterException("Почта не может быть пустой");
        }
        for (User user : findAll().values()) {
            if (Objects.equals(user.getEmail(), email)) {
                throw new ValidationException("Пользователь уже зарегистрирован");
            }
        }
    }
}
