package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exceptions.ItemAccessDeniedException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryItemDao implements ItemDao{

    private final Map<Long, Item> items = new HashMap<>();
    private long currentId = 1;

    @Override
    public Item createItem(Item item) {
        item.setId(currentId);
        items.put(currentId, item);
        currentId++;
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        long itemId = item.getId();
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Не найден предмет с ID " + itemId);
        }
        Item updatedItem = items.get(itemId);

        if (!updatedItem.getOwner().equals(item.getOwner())) {
            throw new ItemAccessDeniedException("Доступ запрещен для " +
                    "userId: " + item.getOwner() + " и itemId: " + itemId);
        }
        String name = item.getName();
        if (name != null) {
            updatedItem.setName(name);
        }

        String description = item.getDescription();
        if (description != null) {
            updatedItem.setDescription(description);
        }

        Boolean available = item.getAvailable();
        if (available != null) {
            updatedItem.setAvailable(available);
        }

        return updatedItem;
    }

    @Override
    public Item getItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Не найден предмет с ID " + itemId);
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().equals(userId)) result.add(item);
        }
        return result;
    }

    @Override
    public List<Item> getItemsBySearch(String text) {
        List<Item> result = new ArrayList<>();
        String wantedItem = text.toLowerCase();

        for (Item item : items.values()) {
            String itemName = item.getName().toLowerCase();
            String itemDescription = item.getDescription().toLowerCase();

            if ((itemName.contains(wantedItem) || itemDescription.contains(wantedItem))
                    && item.getAvailable().equals(true)) {
                result.add(item);
            }
        }
        return result;
    }
}
