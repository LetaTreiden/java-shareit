package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @InjectMocks
    ItemRequestServiceImpl requestService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRequestRepository requestRepository;

    ItemRequest request = new ItemRequest();
    User requestor = new User();
    Item item = new Item();

    @Test
    void addRequestTest() {
        addRequest();

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(requestor));

        Mockito
                .when(requestRepository.save(any()))
                .thenReturn(request);

        Optional<ItemRequestDto> requestDto = Optional.ofNullable(requestService.addRequest(1L,
                ItemRequestMapper.toRequestDto(request)));

        assertThat(requestDto)
                .isPresent()
                .hasValueSatisfying(addRequestTest -> {
                            assertThat(addRequestTest).hasFieldOrPropertyWithValue("id", request.getId());
                            assertThat(addRequestTest).hasFieldOrPropertyWithValue("requestor",
                                    request.getRequestor());
                            assertThat(addRequestTest).hasFieldOrPropertyWithValue("description",
                                    request.getDescription());
                            assertThat(addRequestTest).hasFieldOrPropertyWithValue("created",
                                    request.getCreated());
                        }
                );
    }

    @Test
    void addRequestUserNotFoundTest() {
        addRequest();

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.addRequest(3L, ItemRequestMapper.toRequestDto(request)));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void addRequestDescriptionIsEmpty() {
        addRequest();
        request.setDescription("");

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(requestor));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> requestService.addRequest(1L, ItemRequestMapper.toRequestDto(request)));

        Assertions.assertEquals("Отсутствует описание для поиска вещи", exception.getMessage());
    }

    @Test
    void findAllOwnRequestTest() {
        addRequest();

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(requestor));

        Mockito
                .when(requestRepository.findByRequestorOrderByCreatedDesc(any()))
                .thenReturn(List.of(request));

        List<RequestDto> requestDtos = requestService.findAllOwnRequest(1L);

        Assertions.assertEquals(request.getId(), requestDtos.get(0).getId());
    }

    @Test
    void findAllOwnRequestWithItemTest() {
        addRequest();
        addItem();

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(requestor));

        Mockito
                .when(requestRepository.findByRequestorOrderByCreatedDesc(any()))
                .thenReturn(List.of(request));

        Mockito
                .when(itemRepository.findItemByRequest(Mockito.anyLong()))
                .thenReturn(List.of(item));

        List<RequestDto> requestDtos = requestService.findAllOwnRequest(1L);

        Assertions.assertEquals(request.getId(), requestDtos.get(0).getId());
    }

    @Test
    void findAllOwnRequestNotFoundUserTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.findAllOwnRequest(5L));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void findAllRequestWithPageableTest() {
        addRequest();
        addItem();
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request);
        request.setCreated(LocalDateTime.now().minusHours(5));
        request.setId(2L);
        requests.add(request);
        Page<ItemRequest> page = new PageImpl<>(requests);

        Mockito
                .when(requestRepository.findAllBy(Mockito.anyLong(), any()))
                .thenReturn(page);

        Mockito
                .when(itemRepository.findByRequestId(Mockito.anyLong()))
                .thenReturn(items);

        List<RequestDto> requestDtos = requestService.findAllRequest(1L, 0, 1);

        Assertions.assertEquals(request.getId(), requestDtos.get(0).getId());

    }

    @Test
    void findAllRequestTest() {
        addRequest();
        addItem();
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request);
        request.setCreated(LocalDateTime.now().minusHours(5));
        request.setId(2L);
        requests.add(request);
        Page<ItemRequest> page = new PageImpl<>(requests);

        Mockito
                .when(requestRepository.findAll())
                .thenReturn(requests);

        Mockito
                .when(itemRepository.findByRequestId(Mockito.anyLong()))
                .thenReturn(items);

        List<RequestDto> requestDtos = requestService.findAllRequest(1L, null, null);

        Assertions.assertEquals(request.getId(), requestDtos.get(0).getId());

    }

    @Test
    void findRequestTest() {
        addRequest();
        addItem();

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(requestor));

        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(request));

        Optional<RequestDto> requestDto = Optional.ofNullable(requestService.findRequest(1L, 1L));

        assertThat(requestDto)
                .isPresent()
                .hasValueSatisfying(addRequestTest -> {
                            assertThat(addRequestTest).hasFieldOrPropertyWithValue("id", request.getId());
                            assertThat(addRequestTest).hasFieldOrPropertyWithValue("requestor",
                                    request.getRequestor());
                            assertThat(addRequestTest).hasFieldOrPropertyWithValue("description",
                                    request.getDescription());
                            assertThat(addRequestTest).hasFieldOrPropertyWithValue("created",
                                    request.getCreated());
                        }
                );

    }

    @Test
    void findRequestNotFounUserTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.findRequest(3L, 1L));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());

    }

    @Test
    void findRequestNotFounTest() {
        addUser();

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(requestor));

        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.findRequest(3L, 1L));

        Assertions.assertEquals("Запрос не найден", exception.getMessage());

    }

    private void addUser() {
        requestor.setId(1L);
        requestor.setName("Leo");
        requestor.setEmail("leo@angel.com");
    }

    private void addRequest() {
        addUser();
        request.setId(1L);
        request.setRequestor(requestor);
        request.setDescription("I need a fork");
        request.setCreated(LocalDateTime.now());
    }

    private void addItem() {
        User owner = new User();
        owner.setId(2L);
        owner.setName("Buffy");
        owner.setEmail("buffy@vampire.com");
        item.setId(1L);
        item.setName("Fork");
        item.setOwner(owner);
        item.setAvailable(true);
        item.setDescription("Designed for food");
        item.setRequest(request);
    }
}