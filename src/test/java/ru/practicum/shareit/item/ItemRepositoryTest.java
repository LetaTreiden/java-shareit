package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Slf4j
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    private final Item item = new Item();
    private final User user = new User();
    private final ItemRequest request = new ItemRequest();

    @Test
    void findItemByRequestJpaTest() {
        addItem();
        addRequest();
        item.setRequestId(request);
        Item itemPersist = em.persist(item);
        List<Item> items = itemRepository.findByRequest(request.getId());
        assertThat(itemPersist).isEqualTo(items.get(1));
        assertThat(itemPersist.getId()).isEqualTo(items.get(1).getId());
    }

    @Test
    void findByOwnerJpaTest() {
        addItem();
        Item itemPersist = em.persist(item);
        List<Item> items = itemRepository.findByOwner(user);
        assertThat(itemPersist).isEqualTo(items.get(1));
        assertThat(itemPersist.getId()).isEqualTo(items.get(1).getId());
    }

    @Test
    void findItemsByNameOrDescriptionJpaTest() {
        addItem();
        Item itemPersist = em.persist(item);
        List<Item> items = itemRepository.findItemsByNameOrDescription("S");
        log.info(items.toString());
        assertThat(itemPersist).isEqualTo(items.get(4));
        assertThat(itemPersist.getId()).isEqualTo(items.get(4).getId());
    }

    @Test
    void findByRequestIdJpaTest() {
        addItem();
        addRequest();
        item.setRequestId(request);
        Item itemPersist = em.persist(item);
        List<Item> items = itemRepository.findByRequest(request.getId());
        assertThat(itemPersist).isEqualTo(items.get(1));
        assertThat(itemPersist.getId()).isEqualTo(items.get(1).getId());
    }

    private void addItem() {
        addUser();
        item.setName("Sword");
        item.setOwner(user);
        item.setAvailable(true);
        item.setDescription("For fights");
    }

    private void addUser() {
        user.setId(1L);
        user.setName("Aelin");
        user.setEmail("aelin@whitethorn.com");
    }

    private void addRequest() {
        User requester = new User();
        requester.setId(3L);
        requester.setName("Dorin");
        requester.setEmail("dorin@havilliard.com");
        request.setId(1L);
        request.setRequester(requester);
        request.setDescription("waiting for fight");
        request.setCreated(LocalDateTime.now());
    }
}