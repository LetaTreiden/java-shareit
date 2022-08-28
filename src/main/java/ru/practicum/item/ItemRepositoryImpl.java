package ru.practicum.item;

import org.springframework.stereotype.Service;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemRepositoryImpl implements ItemRepository {
    private static long iId = 0;

    private static Long generateId() {
        return iId++;
    }

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Long uId, Item item, User user) {
        item.setId(generateId());
        item.setOwner(user);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findById(Long id) {
        return items.get(id);
    }

    @Override
    public Collection<Item> findByUserId(Long id) {
        return findAll().values().stream().filter(i -> Objects.equals(i.getOwner().getId(), id)).collect(Collectors.toList());
    }

    @Override
    public Map<Long, Item> findAll() {
        return items;
    }

    @Override
    public Item update(Long id, Item item) {
        Item itemUpdated = findById(id);

        if (item.getName() != null) {
            itemUpdated.setName(item.getName());
        }

        if (item.getDescription() != null) {
            itemUpdated.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            itemUpdated.setAvailable(item.getAvailable());
        }

        return itemUpdated;
    }

    @Override
    public Long delete(Long id) {
        return items.remove(id).getId();
    }

    @Override
    public Collection<Item> search(String text) {
        return findAll().values().stream().filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase()) || i.getDescription().toLowerCase().contains(text.toLowerCase())) && i.getAvailable()).collect(Collectors.toList());
    }

    @Override
    public boolean checkOwner(Long uId, Long iId) {
        return !items.get(iId).getOwner().getId().equals(uId);
    }

    @Override
    public void checkItemId(Long itemId) throws NotFoundException {
        if (!findAll().containsKey(itemId)) {
            throw new NotFoundException("Пользователя с таким id не существует", "User");
        }
    }
}
