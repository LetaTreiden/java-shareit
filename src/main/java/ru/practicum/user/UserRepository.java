package ru.practicum.user;

import org.springframework.stereotype.Repository;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;

import java.util.Map;

@Repository
public interface UserRepository {
    User create(User user);

    User findById(Long userId);

    Map<Long, User> findAll();

    User update(Long userId, User user);

    Long delete(Long userId);

    void checkId(Long userId) throws NotFoundException;

    void checkEmail(String email) throws ValidationException;
}