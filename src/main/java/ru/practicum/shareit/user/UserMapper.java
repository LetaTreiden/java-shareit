package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDTOWithBookings;
import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class UserMapper {

    public static UserDTO toUserDto(User user) {
        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static User toUser(UserDTO userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static User toUser(ItemDTOWithBookings.User user) {
        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setName(user.getName());
        return newUser;
    }

    public static List<UserDTO> mapToUserDto(Iterable<User> users) {
        List<UserDTO> dtos = new ArrayList<>();
        for (User user : users) {
            dtos.add(toUserDto(user));
        }
        return dtos;
    }

    public static ItemDTOWithBookings.User toUserToItemWithBookingsDto(User user) {
        return new ItemDTOWithBookings.User(user.getId(), user.getName(), user.getEmail());
    }

    public static ItemDTO.User toUserToItemDto(User user) {
        return new ItemDTO.User(user.getId(), user.getName(), user.getEmail());
    }

    public static List<User> mapToUser(Iterable<UserDTO> users) {
        List<User> dtos = new ArrayList<>();
        for (UserDTO user : users) {
            dtos.add(toUser(user));
        }
        return dtos;
    }

    public static User toUser(RequestDTO.User userRequest) {
        User user = new User();
        user.setId(userRequest.getId());
        user.setName(userRequest.getName());
        return user;
    }

    public static RequestDTO.User toUserToRequest(User user) {
        return new RequestDTO.User(user.getId(), user.getName());
    }

    public static BookingDTOToReturn.User toUserToBookingDTO(User user) {
        return new BookingDTOToReturn.User(user.getId(), user.getName());
    }

    public static User toUser(BookingDTOToReturn.User userFromDto) {
        User user = new User();
        user.setId(userFromDto.getId());
        user.setName(userFromDto.getName());
        return user;
    }
}
