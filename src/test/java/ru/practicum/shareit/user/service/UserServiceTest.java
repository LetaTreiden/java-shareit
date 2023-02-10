package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserServiceImpl mockUserService;

    @Mock
    UserRepository userRepository;

    @Test
    void addUserTest() {
        User user = new User();
        user.setId(1L);
        user.setName("Buffy");
        user.setEmail("buffy@vampire.com");

        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);

        Optional<UserDTO> userDto = Optional.ofNullable(mockUserService.create(UserMapper.toUserDto(user)));
        assertThat(userDto)
                .isPresent()
                .hasValueSatisfying(addUserTest -> {
                            assertThat(addUserTest).hasFieldOrPropertyWithValue("id", user.getId());
                            assertThat(addUserTest).hasFieldOrPropertyWithValue("name", user.getName());
                            assertThat(addUserTest).hasFieldOrPropertyWithValue("email", user.getEmail());
                        }
                );
    }

    @Test
    void updateUserExceptionTest() {
        UserDTO user = new UserDTO();
        user.setName("Buffy");
        user.setEmail("buffy@vampire.com");

        Mockito
                .when(userRepository.findById(2L))
                .thenThrow(new NotFoundException("Пользователь не найден"));


        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> mockUserService.update(2L, user));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());


    }

    @Test
    void updateUserEmptyTest() {
        UserDTO user = new UserDTO();
        user.setName("Buffy");
        user.setEmail("buffy@vampire.com");
        Mockito
                .when(userRepository.findById(0L))
                .thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> mockUserService.update(0L, user));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void updateUserTest() {
        User user = new User();
        user.setId(1L);
        user.setName("Buffy");
        user.setEmail("buffy@vampire.com");

        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);

        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Optional<UserDTO> userDto = Optional.ofNullable(mockUserService.update(1L, (UserMapper.toUserDto(user))));
        assertThat(userDto)
                .isPresent()
                .hasValueSatisfying(addUserTest -> {
                            assertThat(addUserTest).hasFieldOrPropertyWithValue("id", user.getId());
                            assertThat(addUserTest).hasFieldOrPropertyWithValue("name", user.getName());
                            assertThat(addUserTest).hasFieldOrPropertyWithValue("email", user.getEmail());
                        }
                );
    }

    @Test
    void deleteUserExceptionTest() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> mockUserService.delete(1L));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void deleteUserTest() {
        User user = new User();
        user.setId(1L);
        user.setName("Buffy");
        user.setEmail("buffy@vampire.com");

        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        mockUserService.delete(1L);
        Mockito.verify(userRepository).deleteById(1L);

    }

    @Test
    void getAllUsersEmptyTest() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(new ArrayList<>());
        List<UserDTO> userDtoList = mockUserService.getAll();
        Assertions.assertEquals(userDtoList, new ArrayList<>());


    }

    @Test
    void getAllUsersTest() {
        User user = new User();
        user.setId(1L);
        user.setName("Buffy");
        user.setEmail("buffy@vampire.com");

        User user1 = new User();
        user1.setId(2L);
        user1.setName("Leo");
        user1.setEmail("leo@angel.com");

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user);

        Mockito
                .when(userRepository.findAll())
                .thenReturn(users);

        List<User> getUsers = UserMapper.mapToUser(mockUserService.getAll());
        Assertions.assertEquals(getUsers, users);
    }

    @Test
    void getUserExceptionTest() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> mockUserService.get(1L));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getUserTest() {
        User user = new User();
        user.setId(1L);
        user.setName("Buffy");
        user.setEmail("buffy@vampire.com");

        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Optional<UserDTO> userDto = Optional.ofNullable(mockUserService.get(1L));
        assertThat(userDto)
                .isPresent()
                .hasValueSatisfying(addUserTest -> {
                            assertThat(addUserTest).hasFieldOrPropertyWithValue("id", user.getId());
                            assertThat(addUserTest).hasFieldOrPropertyWithValue("name", user.getName());
                            assertThat(addUserTest).hasFieldOrPropertyWithValue("email", user.getEmail());
                        }
                );
    }
}