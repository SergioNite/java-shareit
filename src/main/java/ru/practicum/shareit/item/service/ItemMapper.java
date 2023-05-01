package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResultDba;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
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

    public static ItemDto toDtoItemFromItemResultDba(ItemResultDba item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getItemId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setLastBooking(bookingFromItemResultDba("last", item));
        itemDto.setNextBooking(bookingFromItemResultDba("next", item));
        return itemDto;
    }

    public static BookingDtoItem bookingFromItemResultDba(String prefix, ItemResultDba itemResultDba) {
        if (itemResultDba == null) return null;

        BookingDtoItem dto = new BookingDtoItem();
        if (prefix.equals("last")) {
            if (itemResultDba.getLastBookingId() == null) {
                return null;
            }
            dto.setId(itemResultDba.getLastBookingId());
            dto.setBookerId(itemResultDba.getLastBookerId());
            dto.setStart(itemResultDba.getLastStartTime());
            dto.setEnd(itemResultDba.getLastEndTime());
        } else if (prefix.equals("next")) {
            if (itemResultDba.getNextBookingId() == null) {
                return null;
            }
            dto.setId(itemResultDba.getNextBookingId());
            dto.setBookerId(itemResultDba.getNextBookerId());
            dto.setStart(itemResultDba.getNextStartTime());
            dto.setEnd(itemResultDba.getNextEndTime());
        }
        return dto;
    }
}
