package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            " and i.available = TRUE")
    List<Item> search(String text);

    List<Item> findByOwnerId(Long ownerId);

    Item findByIdAndOwnerIdNot(Long itemId, Long ownerId);

    List<Item> findByItemRequestId(Long id);
}
