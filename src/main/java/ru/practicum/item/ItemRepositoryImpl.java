package ru.practicum.item;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemRepositoryImpl implements ItemRepository{
    private static long iId = 0;

    private static Long generateItemId() {
        return iId++;
    }

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Long userId, Item item, User user) {
        item.setId(generateItemId());
        item.setOwner(user);

        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<Item> findByUserId(Long userId) {
        return findAll()
                .values()
                .stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Item> findAll() {
        return null;
    }

    @Override
    public Item update(Long itemId, Item item) {
        return null;
    }

    @Override
    public Long delete(Long itemId) {
        return null;
    }

    @Override
    public Collection<Item> search(String text) {
        return null;
    }

    @Override
    public boolean checkOwner(Long userId, Long itemId) {
        return false;
    }

    @Override
    public void checkItemId(Long itemId) throws HttpClientErrorException.NotFound {

    }
}
