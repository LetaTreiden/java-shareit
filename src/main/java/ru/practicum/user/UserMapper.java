package ru.practicum.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toUserDTO(User user) {
        return UserDTO.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).build();
    }

    public User toUser(UserDTO userDto) {
        return User.builder().id(userDto.getId()).name(userDto.getName()).email(userDto.getEmail()).build();
    }
}
