package ru.practicum.shareit.booking;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@DataJpaTest
class BookingRepositoryIntegrationTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    PageRequest page;

    @BeforeEach
    void beforeEach() {
        owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        booking = new Booking();
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);
        page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));
    }

    @Test
    void findByBooker() {
        Page<Booking> actual = bookingRepository.findByBooker(booker, page);
        assertFalse(actual.getContent().isEmpty());
    }

    @Test
    void findByItemOwner() {
        Page<Booking> actual = bookingRepository.findByItemOwner(owner, page);
        assertFalse(actual.getContent().isEmpty());
    }

    @Test
    void findByBookerAndStatus() {
        Page<Booking> actual = bookingRepository.findByBookerAndStatus(booker, Status.APPROVED, page);
        assertFalse(actual.getContent().isEmpty());
    }

    @Test
    void findByItemOwnerAndStatus() {
        Page<Booking> actual = bookingRepository.findByItemOwnerAndStatus(owner, Status.APPROVED, page);
        assertFalse(actual.getContent().isEmpty());
    }

    @Test
    void findByItemOwnerIdAndId() {
        Booking actual = bookingRepository.findByItemOwnerIdAndId(owner.getId(), booking.getId());
        assertEquals(actual, booking);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}