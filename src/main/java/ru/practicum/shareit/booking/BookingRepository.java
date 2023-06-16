package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collection;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findByBookerOrderByStartDesc(User user);

    Collection<Booking> findByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime now);

    Collection<Booking> findByItemOwnerOrderByStartDesc(User user);

    Collection<Booking> findByItemOwnerAndStartAfterOrderByStartDesc(User user, LocalDateTime now);

    @Query(" select b from Booking b " +
            "where (b.booker.id = ?1" +
            " or b.item.owner.id = ?1) " +
            " and b.id = ?2")
    Booking findByIdForOwnerOrBooker(Long userId, Long bookingId);

    Booking findByItemOwnerIdAndId(Long userId, Long bookingId);

    Booking findFirst1ByItemIdAndStatusAndStartBeforeAndBookerId(Long id, Status status, LocalDateTime now,
                                                                 Long bookerId);

    Collection<Booking> findByBookerAndStatusOrderByStartDesc(User user, Status waiting);

    Collection<Booking> findByItemOwnerAndStatusOrderByStartDesc(User user, Status waiting);

    Booking findFirst1ByItemIdAndStartBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime now, Status status);

    Booking findFirst1ByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, Status status);

    Collection<Booking> findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime now,
                                                                              LocalDateTime now1);

    Collection<Booking> findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime now,
                                                                                 LocalDateTime now1);

    Collection<Booking> findByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime now);

    Collection<Booking> findByItemOwnerAndEndBeforeOrderByStartDesc(User user, LocalDateTime now);
}
