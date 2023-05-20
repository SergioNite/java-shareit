package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {
    public static ItemDto toDtoItem(Item item, Booking lastBooking,
                                    Booking nextBooking,
                                    List<Comment> comments) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setLastBooking(BookingMapper.bookingInItemDto(lastBooking));
        itemDto.setNextBooking(BookingMapper.bookingInItemDto(nextBooking));
        if (comments != null) {
            itemDto.setComments(CommentMapper.toCommentDtoList(comments));
        }
        return itemDto;
    }

    public ItemDto toDtoItem(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public Item toItemModel(ItemDto itemDto, User owner) {
        return new Item(
                itemDto.getId() != null ? itemDto.getId() : null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner
        );
    }

    public List<ItemDto> mapItemListToDto(List<Item> userItems) {
        if (userItems.isEmpty()) {
            return new ArrayList<>();
        }

        List<ItemDto> result = new ArrayList<>();
        for (Item item : userItems) {
            ItemDto itemDto = toDtoItem(item, null, null, null);
            result.add(itemDto);
        }
        return result;
    }

}
