package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Transactional
    @Override
    public UserDTO createUser(UserDTO userDto) throws BadRequestException {
        User user = repository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDTO updateUser(Long userId, UserDTO userDto) throws BadRequestException {
        Optional<User> userOptional = repository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = userOptional.get();
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(repository.save(user));
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        repository.deleteById(id);
    }

    @Override
    public Collection<UserDTO> getAllUsers() {
        return UserMapper.mapToUserDto(repository.findAll());
    }

    @Override
    public UserDTO getUser(Long id) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return UserMapper.toUserDto(user.get());
    }
}
