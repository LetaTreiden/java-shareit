package ru.practicum.user;

import ru.practicum.exceptions.ValidationException;
import java.util.Map;

public interface UserRepository {
    User create(User user);

    User findById(Long userId);

    Map<Long, User> findAll();

    User update(Long userId, User user);

    Long delete(Long userId);

    void checkId(Long userId) throws ClassNotFoundException;

    void checkEmail(String email) throws ValidationException;
}