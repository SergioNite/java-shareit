package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingMapperShort;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.storage.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemAccessDeniedException;
import ru.practicum.shareit.item.exceptions.ItemNotAvailibleException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exceptions.EmailErrorException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {

        Item item = mapper.toItemModel(itemDto, null);

        if (item.getAvailable() == null
                || item.getName() == null || item.getName().isEmpty()
                || item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ItemNotAvailibleException("createItem: Не указаны обязательные поля для предмета");
        }

        User ownerUser = userRepository.findById(userId).orElseThrow(
                () -> new ItemNotFoundException("createItem: User not found " + userId)
        );

        item.setOwner(ownerUser);

        try {
            item = itemRepository.save(item);
            return mapper.toDtoItem(item, null, null, null);
        } catch (DataIntegrityViolationException e) {
            throw new ItemNotAvailibleException("createItem: Не удалось сохранить данные в БД");
        }

    }

    @Override
    public CommentDto addComment(long itemId, long authorId, CommentDtoRequest commentDto) {
        User ownerUser = userRepository.findById(authorId).orElseThrow(
                () -> new UserNotFoundException("addComment: User not found " + authorId)
        );
        Optional<Booking> booking = bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(authorId, itemId, LocalDateTime.now());
        if (booking.isEmpty()) {
            throw new EmailErrorException("addComment: Cannt find booking item " + itemId);
        }
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException("addComment: Item not found " + itemId)
        );

        Comment comment = commentMapper.toCommentModel(
                commentDto, item, ownerUser);
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException("updateItem: Wrong itemId " + itemId)
        );
        checkOwnerId(item, userId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        List<Comment> comments = commentRepository.findByItemId(itemId);
        return mapper.toDtoItem(itemRepository.save(item), null, null, comments);
    }

    private void checkOwnerId(Item item, long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new UserNotFoundException("checkOwnerId: Неизвестный владелец " + ownerId)
        );
        if (item.getOwner().getId() != owner.getId()) {
            throw new ItemAccessDeniedException("checkOwnerId: Неверный владелец " + ownerId);
        }
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Wrong id"));
        List<Comment> comments = commentRepository.findByItemId(itemId);
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            Sort sortDesc = Sort.by("start").descending();
            return getOwnerItemDto(item, now, sortDesc, comments);
        } else {
            return mapper.toDtoItem(item, null, null, comments);
        }
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Owner not found"));

        List<Item> ownerItems = itemRepository.findAllByOwner(owner);

        List<ItemDto> result = ownerItems.stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(mapper::toDtoItem)
                .collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();

        List<Booking> last = bookingRepository.findAllByItemInAndStartLessThanEqualAndStatusIsOrderByStartDesc(
                ownerItems, now, BookingStatus.APPROVED);
        List<Booking> next = bookingRepository.findAllByItemInAndStartAfterAndStatusIsOrderByStartAsc(
                ownerItems, now, BookingStatus.APPROVED);
        Map<Long, List<Booking>> itemIdToListLast = last.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId(), Collectors.toList()));
        Map<Long, List<Booking>> itemIdToListNext = next.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId(), Collectors.toList()));

        result.forEach(i -> i.setLastBooking(getBookingDtoShort(i.getId(), itemIdToListLast)));
        result.forEach(i -> i.setNextBooking(getBookingDtoShort(i.getId(), itemIdToListNext)));

        return result;
    }
    private BookingDtoItem getBookingDtoShort(Long itemDtoEnhancedId, Map<Long, List<Booking>> itemIdToListBooking) {
        Optional<BookingDtoItem> bookingDtoShort = Optional.empty();
        if (itemIdToListBooking.containsKey(itemDtoEnhancedId)) {
            bookingDtoShort = itemIdToListBooking.get(itemDtoEnhancedId).stream()
                    .map(BookingMapperShort::bookingToBookingDtoShort).findFirst();
        }
        return bookingDtoShort.orElse(null);
    }
    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        if (Objects.isNull(text) || text.isBlank() || text.length() <= 1) {
            return new ArrayList<>();
        }
        return mapper.mapItemListToDto(itemRepository.search(text));
    }


    private ItemDto getOwnerItemDto(Item item, LocalDateTime now, Sort sort, List<Comment> comments) {
        Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                item.getId(),
                BookingStatus.APPROVED,
                LocalDateTime.now()
        );
        Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStart(
                item.getId(),
                BookingStatus.APPROVED,
                LocalDateTime.now());

        return ItemMapper.toDtoItem(item,
                (lastBooking.isEmpty() ? null : lastBooking.get()),
                (nextBooking.isEmpty() ? null : nextBooking.get()),
                comments);
    }

}
