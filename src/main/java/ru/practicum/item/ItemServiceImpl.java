package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.item.*;
import ru.practicum.user.*;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService uService;
    private final UserMapper uMapper;

    private final ItemRepository iRepository;
    private final ItemMapper iMapper;

    @Override
    public ItemDTO createItem(Long uId, ItemDTO iDto) throws ValidationException, ClassNotFoundException {
        uService.checkId(uId);

        if (!StringUtils.hasText(iDto.getName())) {
            throw new ValidationException("Имя не может быть пустым", "CreateItem");
        }

        if (iDto.getDescription() == null) {
            throw new ValidationException("Описание не может быть пустым", "CreateItem");
        }

        if (iDto.getAvailable() == null) {
            throw new ValidationException("Статус доступности не модет быть пустым", "CreateItem");
        }

        UserDTO uDto = uService.findById(uId);
        Item item = iRepository.create(uId, iMapper.toItem(iDto), uMapper.toUser(uDto));

        log.info("Товар id {} создан", item.getId());
        return iMapper.toIDto(item);
    }

        @Override
        public ItemDTO findById (Long id) throws HttpClientErrorException.NotFound {
            iRepository.checkItemId(id);
            Item item = iRepository.findById(id);
            return iMapper.toIDto(item);
        }

        @Override
        public Collection<ItemDTO> findByUser (Long id) throws HttpClientErrorException.NotFound, ClassNotFoundException {
            uService.checkId(id);

            return iRepository.findByUserId(id).stream()
                    .map(iMapper::toIDto)
                    .collect(Collectors.toList());
        }

        @Override
        public ItemDTO update (Long userId, Long itemId, ItemDTO iDto) throws HttpClientErrorException.NotFound, ClassNotFoundException {
            uService.checkId(userId);
            iRepository.checkItemId(itemId);

            if (iRepository.checkOwner(userId, itemId)) {
                throw new ClassNotFoundException("Неправльный владелец");
            }

            Item item = iRepository.update(itemId, iMapper.toItem(iDto));
            log.info("Обновлено id {}", item.getId());
            return iMapper.toIDto(item);
        }

        @Override
        public Long deleteItem (Long userId, Long itemId) throws HttpClientErrorException.NotFound, ClassNotFoundException {
            uService.checkId(userId);
            iRepository.checkItemId(itemId);

            Long itemDeletedId = iRepository.delete(itemId);
            log.info("Удалено id {}", itemDeletedId);
            return itemDeletedId;
        }

        @Override
        public Collection<ItemDTO> search (String text){
            if (!StringUtils.hasText(text)) {
                return Collections.emptyList();
            }

            return iRepository.search(text)
                    .stream()
                    .map(iMapper::toIDto)
                    .collect(Collectors.toList());
        }

}
