package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
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

    @Override
    public Item createItem(Item item) {
        boolean ownerExists = isUserExists(item.getOwner());
        if (!ownerExists) {
            throw new ItemNotFoundException("Не найден владелец с ID " + item.getOwner());
        }
        if (Objects.isNull(item.getAvailable())
                || Objects.isNull(item.getName()) || item.getName().isEmpty()
                || Objects.isNull(item.getDescription()) || item.getDescription().isEmpty()) {
            throw new ItemNotAvailibleException("Не указаны обязательные поля для предмета");
        }
        return itemDao.createItem(item);
    }

    @Override
    public Item updateItem(Item item) {
        return itemDao.updateItem(item);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemDao.getItemById(itemId);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        return itemDao.getAllItems(userId);
    }

    @Override
    public List<Item> getItemsBySearch(String text) {
        if (Objects.isNull(text) || text.isBlank() || text.length() <= 1) {
            return new ArrayList<>();
        }
        return itemDao.getItemsBySearch(text);
    }

    private boolean isUserExists(long userId) {
        List<User> users = userDao.findAllUsers();
        List<User> result = users.stream().filter(user -> user.getId() == userId).collect(Collectors.toList());
        return result.size() > 0;
    }
}
