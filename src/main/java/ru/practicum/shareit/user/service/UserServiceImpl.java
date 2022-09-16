package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO create(UserDTO uDto) throws ValidationException {
        userRepository.checkEmail(uDto.getEmail());
        User user = userRepository.create(userMapper.toUser(uDto));
        log.info("Пользователь создан");
        return userMapper.toUserDTO(user);
    }

    @Override
    public UserDTO findById(Long id) throws NotFoundException {
        checkId(id);
        User user = userRepository.findById(id);
        log.info("Получен пользователь");
        return userMapper.toUserDTO(user);
    }

    @Override
    public Collection<UserDTO> findAll() {
        return userRepository.findAll().values().stream().map(userMapper::toUserDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO update(Long id, UserDTO uDto) throws ValidationException, NotFoundException {
        checkId(id);
        if (StringUtils.hasText(uDto.getEmail())) {
            userRepository.checkEmail(uDto.getEmail());
        }
        User user = userRepository.update(id, userMapper.toUser(uDto));
        log.info("Пользователь обновлен");
        return userMapper.toUserDTO(user);
    }

    @Override
    public Long delete(Long id) throws HttpClientErrorException.NotFound, NotFoundException {
        checkId(id);
        Long userDelId = userRepository.delete(id);
        log.info("Пользователь удален");
        return userDelId;
    }

    @Override
    public void checkId(Long id) throws HttpClientErrorException.NotFound, NotFoundException {
        userRepository.checkId(id);
    }
}