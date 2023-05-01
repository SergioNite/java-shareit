package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemResultDba;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Transactional
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(" select i from items i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')) and i.available = true")
    List<Item> search(String text);

    List<Item> findAllByOwner(User owner);

    boolean existsItemByIdAndAvailableIsTrue(long itemId);

}
