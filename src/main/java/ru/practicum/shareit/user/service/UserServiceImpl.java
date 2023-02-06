package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserDTOToUpd;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Transactional
    @Override
    public UserDTO create(UserDTO userDto) {

        User user = repository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDTOToUpd update(Long userId, UserDTO userDto) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDtoToUpd(user);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        repository.deleteById(id);
    }

    @Override
    public List<UserDTO> getAll() {
        return UserMapper.mapToUserDto(repository.findAll());
    }

    @Override
    public UserDTO get(Long id) {
        Optional<User> user = Optional.ofNullable(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found")));
        return UserMapper.toUserDto(user.get());
    }
}
