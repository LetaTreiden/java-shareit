package ru.practicum.shareit.item;

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
        item.setRequest(request);
        Item itemPersist = em.persist(item);
        List<Item> items = itemRepository.findItemByRequest(request.getId());
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
        List<Item> items = itemRepository.findItemsByNameOrDescription("F");
        assertThat(itemPersist).isEqualTo(items.get(3));
        assertThat(itemPersist.getId()).isEqualTo(items.get(3).getId());
    }

    @Test
    void findByRequestIdJpaTest() {
        addItem();
        addRequest();
        item.setRequest(request);
        Item itemPersist = em.persist(item);
        List<Item> items = itemRepository.findItemByRequest(request.getId());
        assertThat(itemPersist).isEqualTo(items.get(1));
        assertThat(itemPersist.getId()).isEqualTo(items.get(1).getId());
    }

    private void addItem() {
        addUser();
        item.setName("Fork");
        item.setOwner(user);
        item.setAvailable(true);
        item.setDescription("Designed for food");
    }

    private void addUser() {
        user.setId(1L);
        user.setName("Buffy");
        user.setEmail("buffy@vampire.com");
    }

    private void addRequest() {
        User requestor = new User();
        requestor.setId(3L);
        requestor.setName("Kat");
        requestor.setEmail("Kat@kat.com");
        request.setId(1L);
        request.setRequestor(requestor);
        request.setDescription("I need a fork to eat");
        request.setCreated(LocalDateTime.now());
    }

}