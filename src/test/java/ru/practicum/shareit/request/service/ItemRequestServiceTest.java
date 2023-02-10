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
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.request.dto.RequestDTOWithItems;
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
    RequestService requestService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    RequestRepository requestRepository;

    ItemRequest request = new ItemRequest();
    User requester = new User();
    Item item = new Item();

    @Test
    void addRequestTest() {
        addRequest();

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(requester));

        Mockito
                .when(requestRepository.save(any()))
                .thenReturn(request);

        Optional<RequestDTO> requestDto = Optional.ofNullable(requestService.add(1L,
                RequestMapper.toRequestDto(request)));

        assertThat(requestDto)
                .isPresent()
                .hasValueSatisfying(addRequestTest -> {
                            assertThat(addRequestTest).hasFieldOrPropertyWithValue("id", request.getId());
                            assertThat(addRequestTest).hasFieldOrPropertyWithValue("requester",
                                    request.getRequester());
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
                () -> requestService.add(3L, RequestMapper.toRequestDto(request)));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void addRequestDescriptionIsEmpty() {
        addRequest();
        request.setDescription("");

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(requester));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> requestService.add(1L, RequestMapper.toRequestDto(request)));

        Assertions.assertEquals("Отсутствует описание для поиска вещи", exception.getMessage());
    }

    @Test
    void findAllOwnRequestTest() {
        addRequest();

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(requester));

        Mockito
                .when(requestRepository.findByRequesterOrderByCreatedDesc(any()))
                .thenReturn(List.of(request));

        List<RequestDTOWithItems> requestDtos = requestService.findAllByOwner(1L);

        Assertions.assertEquals(request.getId(), requestDtos.get(0).getId());
    }

    @Test
    void findAllOwnRequestWithItemTest() {
        addRequest();
        addItem();

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(requester));

        Mockito
                .when(requestRepository.findByRequesterOrderByCreatedDesc(any()))
                .thenReturn(List.of(request));

        Mockito
                .when(itemRepository.findByRequest(Mockito.anyLong()))
                .thenReturn(List.of(item));

        List<RequestDTOWithItems> requestDtos = requestService.findAllByOwner(1L);

        Assertions.assertEquals(request.getId(), requestDtos.get(0).getId());
    }

    @Test
    void findAllOwnRequestNotFoundUserTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.findAllByOwner(5L));

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
                .when(itemRepository.findByRequest(Mockito.anyLong()))
                .thenReturn(items);

        List<RequestDTOWithItems> requestDtos = requestService.findAll(1L, 0, 1);

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
                .when(itemRepository.findByRequest(Mockito.anyLong()))
                .thenReturn(items);

        List<RequestDTOWithItems> requestDtos = requestService.findAll(1L, null, null);

        Assertions.assertEquals(request.getId(), requestDtos.get(0).getId());

    }

    @Test
    void findAllRequestSizeOrPageLessZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> requestService.findAll(1L, 1, -1));

        Assertions.assertEquals("From или size не могут принимать отрицательноге значение",
                exception.getMessage());

    }

    @Test
    void findAllRequestSizeEqualZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> requestService.findAll(1L, 1, 0));

        Assertions.assertEquals("Size не может принимать значение 0",
                exception.getMessage());

    }

    @Test
    void findRequestTest() {
        addRequest();
        addItem();

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(requester));

        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(request));

        Optional<RequestDTO> requestDto = Optional.ofNullable(requestService.findById(1L, 1L));

        assertThat(requestDto)
                .isPresent()
                .hasValueSatisfying(addRequestTest -> {
                            assertThat(addRequestTest).hasFieldOrPropertyWithValue("id", request.getId());
                            assertThat(addRequestTest).hasFieldOrPropertyWithValue("requestor",
                                    request.getRequester());
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
                () -> requestService.findById(3L, 1L));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());

    }

    @Test
    void findRequestNotFounTest() {
        addUser();

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(requester));

        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.findById(3L, 1L));

        Assertions.assertEquals("Запрос не найден", exception.getMessage());

    }

    private void addUser() {
        requester.setId(1L);
        requester.setName("Leo");
        requester.setEmail("leo@angel.com");
    }

    private void addRequest() {
        addUser();
        request.setId(1L);
        request.setRequester(requester);
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
        item.setRequestId(request);
    }
}