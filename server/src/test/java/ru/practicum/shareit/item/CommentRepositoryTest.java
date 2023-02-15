package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    private final Comment comment = new Comment();
    private final User user = new User();

    private final Item item = new Item();

    @Test
    public void findByItemJpaTest() {
        addComment();
        Comment commentPersist = em.persist(comment);
        List<Comment> comment2 = commentRepository.findByItem(item);
        addUser();
        assertThat(commentPersist).isEqualTo(comment2.get(1));
        assertThat(commentPersist.getId()).isEqualTo(comment2.get(1).getId());

    }

    private void addComment() {
        addUser();
        addItem();
        comment.setAuthor(user);
        comment.setText("I need a fork");
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
    }

    private void addItem() {
        User user1 = new User();
        user1.setId(2L);
        user1.setName("Cat");
        user1.setEmail("leo@cat.com");
        item.setId(1L);
        item.setName("Fork");
        item.setOwner(user1);
        item.setAvailable(true);
        item.setDescription("Designed for food");
    }

    private void addUser() {
        user.setId(1L);
        user.setName("Leo");
        user.setEmail("leo@angel.com");
    }

}