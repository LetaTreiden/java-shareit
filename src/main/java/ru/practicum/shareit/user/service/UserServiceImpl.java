package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.InvalidParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> findAllUsers() {
        return UserMapper.toUserDTOs(userRepository.findAll());
    }

    public UserDTO findUserById(Long id) {
        validateUser(id);
        return UserMapper.toUserDto(userRepository.getReferenceById(id));
    }

    public UserDTO createUser(UserDTO userDto) {
        validateEmail(userDto);
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    public UserDTO updateUser(UserDTO userDto) {
        UserDTO temp = UserMapper.toUserDto(userRepository.getReferenceById(userDto.getId()));
        if (userDto.getName() != null && !userDto.getName().equals("")) {
            temp.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals("")) {
            temp.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(temp)));
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO patchUser(UserDTO userDto, Long userId) {
        userDto.setId(userId);
        if (findUserById(userDto.getId()) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        UserDTO patchedUser = findUserById(userDto.getId());
        if (userDto.getName() != null) {
            patchedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            for (UserDTO storedUser : findAllUsers()) {
                if (userDto.getEmail().equals(storedUser.getEmail())) {
                    throw new ValidationException("Пользователь с таекой почтой уже существует");
                }
            }
            patchedUser.setEmail(userDto.getEmail());
        }
        return patchedUser;
    }

    private void validateUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validateEmail(UserDTO userDto) {
        if (userDto.getEmail() == null || !userDto.getEmail().contains("@")) {
            throw new InvalidParameterException("Почта не может быть пустой");
        }
    }

}