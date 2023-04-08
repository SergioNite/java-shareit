package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotAvailibleException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemDao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserDao userDao;
    private final ItemDao itemDao;
    private final ItemMapper mapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item item = mapper.toItemModel(itemDto, userId);

        boolean ownerExists = isUserExists(item.getOwner());
        if (!ownerExists) {
            throw new ItemNotFoundException("Не найден владелец с ID " + item.getOwner());
        }
        if (Objects.isNull(item.getAvailable())
                || Objects.isNull(item.getName()) || item.getName().isEmpty()
                || Objects.isNull(item.getDescription()) || item.getDescription().isEmpty()) {
            throw new ItemNotAvailibleException("Не указаны обязательные поля для предмета");
        }
        return mapper.toDtoItem(itemDao.createItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = mapper.toItemModel(itemDto, userId);
        item.setId(itemId);
        return mapper.toDtoItem(itemDao.updateItem(item));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return mapper.toDtoItem(itemDao.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        return mapper.mapItemListToDto(itemDao.getAllItems(userId));
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        if (Objects.isNull(text) || text.isBlank() || text.length() <= 1) {
            return new ArrayList<>();
        }
        return mapper.mapItemListToDto(itemDao.getItemsBySearch(text));
    }

    private boolean isUserExists(long userId) {
        List<User> users = userDao.findAllUsers();
        List<User> result = users.stream().filter(user -> user.getId() == userId).collect(Collectors.toList());
        return result.size() > 0;
    }
}
