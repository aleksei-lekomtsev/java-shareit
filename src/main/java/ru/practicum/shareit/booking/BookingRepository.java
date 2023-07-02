package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBooker(User user, Pageable pageable);

    Page<Booking> findByBookerAndStartAfter(User user, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerAndEndBefore(User user, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerAndStartBeforeAndEndAfter(User user, LocalDateTime now, LocalDateTime now1,
                                                        Pageable pageable);

    Page<Booking> findByBookerAndStatus(User user, Status waiting, Pageable pageable);

    Page<Booking> findByItemOwner(User user, Pageable pageable);

    Page<Booking> findByItemOwnerAndStartAfter(User user, LocalDateTime now, Pageable pageable);

    Page<Booking> findByItemOwnerAndStatus(User user, Status status, Pageable page);

    Page<Booking> findByItemOwnerAndStartBeforeAndEndAfter(User user, LocalDateTime now, LocalDateTime now1,
                                                           Pageable page);

    Page<Booking> findByItemOwnerAndEndBefore(User user, LocalDateTime now, Pageable page);

    Booking findByItemOwnerIdAndId(Long userId, Long bookingId);

    @Query(" select b from Booking b " +
            "where (b.booker.id = :userId" +
            " or b.item.owner.id = :userId) " +
            " and b.id = :bookingId")
    Booking findByIdForOwnerOrBooker(@Param("userId") Long userId, @Param("bookingId") Long bookingId);

    Booking findFirst1ByItemIdAndStatusAndStartBeforeAndBookerId(Long id, Status status, LocalDateTime now,
                                                                 Long bookerId);

    Booking findFirst1ByItemIdAndStartBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime now, Status status);

    Booking findFirst1ByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, Status status);
}
