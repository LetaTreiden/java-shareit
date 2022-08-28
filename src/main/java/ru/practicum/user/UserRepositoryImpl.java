package ru.practicum.user;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;

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
            throw new NotFoundException((String.format("Пользователь id %d не найден�", id)), "User");
        }
    }

    @Override
    public void checkEmail(String email) throws ValidationException {
        if (!StringUtils.hasText(email)) {
            throw new ValidationException("Почта не модет быть пустой", email);
        }

        for (User user : findAll().values()) {
            if (Objects.equals(user.getEmail(), email)) {
                throw new ValidationException(String.format("Почта не модет быть пустой", email), "CheckEmail");
            }
        }
    }
}
